// ======================= 공통 유틸 =======================
function qs(sel, root=document){ return root.querySelector(sel); }
function qsa(sel, root=document){ return Array.from(root.querySelectorAll(sel)); }

// ======================= 네비 active 처리 =======================
function setActiveNav() {
  const path = location.pathname;
  qsa('.nav-link').forEach(a => a.classList.remove('active'));
  const toActivate =
    path.startsWith('/favorites') ? qs('.nav-link[href="/favorites"]') :
    path.startsWith('/notices')  ? qs('.nav-link[href="/notices"]')  :
    qs('.nav-link[href="/notices"]'); // 홈 = 공지
  toActivate?.classList.add('active');
}

// ======================= 검색 =======================
function handleSearch(e) {
  e.preventDefault();
  const q = qs('#searchInput')?.value.trim().toLowerCase() || '';
  const cards = qsa('#noticeList .col');
  let visible = 0;
  cards.forEach(col => {
    const title = col.querySelector('.card-title')?.textContent.toLowerCase() || '';
    const tags  = qsa('.tag-badge', col).map(t=>t.textContent.toLowerCase()).join(' ');
    const show = title.includes(q) || tags.includes(q);
    col.style.display = show ? '' : 'none';
    if (show) visible++;
  });
  qs('#emptyState')?.classList.toggle('d-none', visible !== 0);
}

// ======================= 카테고리 필터 =======================
function filterByCategory(btn) {
  const cat = btn.getAttribute('data-filter');
  qsa('.btn-outline-primary').forEach(b=>b.classList.remove('active'));
  btn.classList.add('active');

  const cards = qsa('#noticeList .col');
  let visible = 0;
  cards.forEach(col => {
    const c = col.getAttribute('data-category');
    const show = (cat === 'ALL') || (c === cat);
    col.style.display = show ? '' : 'none';
    if (show) visible++;
  });
  qs('#emptyState')?.classList.toggle('d-none', visible !== 0);
}

// ======================= 정렬 =======================
function sortNotices(mode) {
  const list = qs('#noticeList');
  const items = qsa('.col', list).filter(el => el.style.display !== 'none');
  items.sort((a,b) => {
    if (mode === 'latest') {
      const da = new Date(a.getAttribute('data-date') || 0);
      const db = new Date(b.getAttribute('data-date') || 0);
      return db - da;
    } else {
      const ga = toDdayValue(a.getAttribute('data-deadline'));
      const gb = toDdayValue(b.getAttribute('data-deadline'));
      return ga - gb;
    }
  });
  items.forEach(i => list.appendChild(i));
}

function toDdayValue(deadlineStr) {
  if (!deadlineStr) return 9999;
  const today = new Date(); today.setHours(0,0,0,0);
  const d = new Date(deadlineStr);
  return Math.ceil((d - today) / (1000*60*60*24));
}

// ======================= D-Day 계산 =======================
function pickTargetDateISO(col) {
  // 1) deadline이 있으면 그걸로 D-day 계산
  const dl = col.getAttribute('data-deadline');
  if (dl && dl.trim() !== '') return dl;

  // 2) 없으면 등록일(date)로 계산
  const d = col.getAttribute('data-date');
  return (d && d.trim() !== '') ? d : null;
}

function calcDdayFromISO(iso) {
  if (!iso) return '';
  const target = new Date(iso);
  const today = new Date();
  const msPerDay = 1000 * 60 * 60 * 24;
  const diff = Math.ceil((target.setHours(0,0,0,0) - today.setHours(0,0,0,0)) / msPerDay);

  if (diff > 0)   return `D-${diff}`;
  if (diff === 0) return 'D-Day';
  return `D+${Math.abs(diff)}`;
}

function decorateDday() {
  document.querySelectorAll('#noticeList .col').forEach(col => {
    const iso = pickTargetDateISO(col); // deadline > date
    const pill = col.querySelector('.dday-pill');
    if (!pill) return;
    const label = calcDdayFromISO(iso);
    pill.textContent = label;
    if (label.startsWith('D-')) {
      const n = Number(label.slice(2));
      if (!Number.isNaN(n) && n <= 3) pill.classList.add('dday-urgent');
    }
  });
}

// ======================= 즐겨찾기 =======================
async function toggleFavorite(button, event) {
  event.stopPropagation();

  const noticeId = Number(button.getAttribute('data-id'));
  if (!noticeId || noticeId <= 0) return;

  try {
    const res = await fetch('/api/favorites/toggle', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ noticeId })
    });

    if (!res.ok) throw new Error('HTTP ' + res.status);

    const data = await res.json();
    const icon = button.querySelector("i");
    if (!icon) return;

    if (data.added) {
      icon.classList.remove('bi-star');
      icon.classList.add('bi-star-fill', 'text-warning');
    } else {
      icon.classList.remove('bi-star-fill', 'text-warning');
      icon.classList.add('bi-star');
    }

  } catch (err) {
    console.error("❌ 즐겨찾기 토글 실패:", err);
    alert('즐겨찾기 처리 중 오류가 발생했습니다.');
  }
}

// 즐겨찾기 상태 복원
async function hydrateStarsFromServer() {
  try {
    const res = await fetch('/api/favorites/ids');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const ids = await res.json();

    qsa('.star-btn').forEach(btn => {
      const id = Number(btn.getAttribute('data-id'));
      const icon = btn.querySelector('i');
      if (!icon) return;
      if (ids.includes(id)) {
        icon.classList.add('bi-star-fill', 'text-warning');
        icon.classList.remove('bi-star');
      } else {
        icon.classList.add('bi-star');
        icon.classList.remove('bi-star-fill', 'text-warning');
      }
    });
  } catch (e) {
    console.warn('hydrateStarsFromServer failed', e);
  }
}

// ======================= 초기화 =======================
document.addEventListener('DOMContentLoaded', () => {
  setActiveNav();
  hydrateStarsFromServer();

  // ✅ D-Day 표시 업데이트
  qsa('[data-date]').forEach(card => {
    const iso = card.getAttribute('data-date');
    const pill = card.querySelector('.dday-pill');
    if (pill) pill.textContent = calcDday(iso);
  });
});

// 전역 노출
window.handleSearch = handleSearch;
window.filterByCategory = filterByCategory;
window.sortNotices = sortNotices;
window.toggleFavorite = toggleFavorite;

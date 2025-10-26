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
      const da = new Date(a.querySelector('.small span')?.textContent || 0);
      const db = new Date(b.querySelector('.small span')?.textContent || 0);
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

function decorateDday() {
  qsa('#noticeList .col').forEach(col => {
    const deadline = col.getAttribute('data-deadline');
    const pill = col.querySelector('.dday-pill');
    if (!pill || !deadline) return;
    const d = toDdayValue(deadline);
    pill.textContent = (d >= 0) ? `D-${d}` : `D+${Math.abs(d)}`;
    if (d <= 3) pill.classList.add('dday-urgent');
  });
}

// ======================= 즐겨찾기 =======================
function toggleFavorite(button, event) {
  event.stopPropagation(); // 카드 링크 클릭 방지

  const noticeId = button.getAttribute('data-id');
  if (!noticeId || noticeId === "0") {
    console.error("⚠️ noticeId가 잘못됨:", noticeId);
    return;
  }

  console.log("⭐ 즐겨찾기 토글 시도:", noticeId);

  fetch(`/api/favorite/toggle?noticeId=${noticeId}`, { method: "POST" })
    .then(res => {
      if (!res.ok) throw new Error("서버 오류");
      return res.json ? res.json() : res; // 혹시 JSON 응답이면
    })
    .then(() => {
      const icon = button.querySelector("i");
      if (!icon) {
        console.warn("⚠️ 아이콘을 찾을 수 없습니다.");
        return;
      }

      // bi-star ↔ bi-star-fill 교체
      if (icon.classList.contains("bi-star")) {
        icon.classList.remove("bi-star");
        icon.classList.add("bi-star-fill", "text-warning"); // 노란색 별
      } else {
        icon.classList.remove("bi-star-fill", "text-warning");
        icon.classList.add("bi-star");
      }

      console.log("✅ 즐겨찾기 토글 완료:", noticeId);
    })
    .catch(err => {
      console.error("❌ 즐겨찾기 토글 실패:", err);
    });
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
      if (ids.includes(id)) {
        icon.classList.add('bi-star-fill');
        icon.classList.remove('bi-star');
      } else {
        icon.classList.add('bi-star');
        icon.classList.remove('bi-star-fill');
      }
    });
  } catch(e) {
    console.warn('hydrateStarsFromServer failed', e);
  }
}

// ======================= 초기화 =======================
document.addEventListener('DOMContentLoaded', () => {
  setActiveNav();
  decorateDday();
  hydrateStarsFromServer();
});

// 전역 노출 (Thymeleaf 템플릿에서 onclick용)
window.handleSearch = handleSearch;
window.filterByCategory = filterByCategory;
window.sortNotices = sortNotices;
window.toggleFavorite = toggleFavorite;

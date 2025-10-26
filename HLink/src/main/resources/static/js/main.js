// ===== 공통 유틸 =====
function qs(sel, root=document){ return root.querySelector(sel); }
function qsa(sel, root=document){ return Array.from(root.querySelectorAll(sel)); }

// ===== 네비 active 처리 =====
function setActiveNav() {
  const path = location.pathname;
  qsa('.nav-link').forEach(a => a.classList.remove('active'));
  const toActivate =
    path.startsWith('/favorites') ? qs('.nav-link[href="/favorites"]') :
    path.startsWith('/notices')  ? qs('.nav-link[href="/notices"]')  :
    qs('.nav-link[href="/notices"]'); // 홈 = 공지
  toActivate?.classList.add('active');
}

// ===== 검색 =====
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

// ===== 카테고리 필터 =====
function filterByCategory(btn) {
  const cat = btn.getAttribute('data-filter');
  // 버튼 active 토글
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

// ===== 정렬 =====
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

// ===== 즐겨찾기(LocalStorage) =====
const STAR_KEY = 'hlink_favorites';
function getStars() {
  try { return JSON.parse(localStorage.getItem(STAR_KEY)) || []; } catch { return []; }
}
function saveStars(arr) { localStorage.setItem(STAR_KEY, JSON.stringify(arr)); }

function toggleFavorite(btn, event) {
  // 링크 열림 방지 (stretched-link 무력화)
  event?.stopPropagation();
  event?.preventDefault();

  const id = btn.getAttribute('data-id');
  const icon = btn.querySelector('i');
  const stars = getStars();
  const idx = stars.indexOf(id);

  if (idx >= 0) {
    stars.splice(idx, 1);
    icon.classList.remove('bi-star-fill');
    icon.classList.add('bi-star');
  } else {
    stars.push(id);
    icon.classList.remove('bi-star');
    icon.classList.add('bi-star-fill');
  }
  saveStars(stars);

  // 즐겨찾기 페이지에선 즉시 리스트 갱신
  if (location.pathname.startsWith('/favorites')) {
    renderFavoritesPage();
  }
}

function hydrateStars() {
  const stars = getStars().map(String); // 모두 문자열로 통일
  document.querySelectorAll('.star-btn').forEach(btn => {
    const id = String(btn.getAttribute('data-id'));
    const icon = btn.querySelector('i');
    if (stars.includes(id)) {
      icon.classList.remove('bi-star');
      icon.classList.add('bi-star-fill');
    } else {
      icon.classList.remove('bi-star-fill');
      icon.classList.add('bi-star');
    }
  });
}

// ===== 즐겨찾기 페이지 렌더링 (클라 사이드 필터) =====
function renderFavoritesPage() {
  const list = qs('#noticeList');
  if (!list) return; // 공지 목록 페이지가 아니면 스킵
  const stars = getStars();
  let visible = 0;
  qsa('.col', list).forEach(col => {
    const id = col.getAttribute('data-id');
    const show = stars.includes(id);
    col.style.display = show ? '' : 'none';
    if (show) visible++;
  });
  qs('#emptyState')?.classList.toggle('d-none', visible !== 0);
}

// ===== 초기화 =====
document.addEventListener('DOMContentLoaded', () => {
  setActiveNav();
  decorateDday();
  hydrateStars();

  // 즐겨찾기 페이지면 화면 필터 적용
  if (location.pathname.startsWith('/favorites')) {
    renderFavoritesPage();
  }
});

// 전역 노출(템플릿 onclick에서 호출)
window.handleSearch = handleSearch;
window.filterByCategory = filterByCategory;
window.sortNotices = sortNotices;
window.toggleFavorite = toggleFavorite;

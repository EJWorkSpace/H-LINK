// ===== 검색 =====
function handleSearch(e) {
  e.preventDefault();
  const q = document.getElementById('searchInput').value.trim().toLowerCase();
  const cards = document.querySelectorAll('#noticeList .col');
  let visible = 0;
  cards.forEach(col => {
    const title = col.querySelector('.card-title')?.textContent.toLowerCase() || '';
    const tags = Array.from(col.querySelectorAll('.tag-badge')).map(t=>t.textContent.toLowerCase()).join(' ');
    const show = title.includes(q) || tags.includes(q);
    col.style.display = show ? '' : 'none';
    if (show) visible++;
  });
  document.getElementById('emptyState')?.classList.toggle('d-none', visible !== 0);
}

// ===== 카테고리 필터 =====
function filterByCategory(btn) {
  const cat = btn.getAttribute('data-filter');
  document.querySelectorAll('.btn-outline-secondary').forEach(b=>b.classList.remove('active'));
  btn.classList.add('active');
  const cards = document.querySelectorAll('#noticeList .col');
  let visible = 0;
  cards.forEach(col => {
    const c = col.getAttribute('data-category');
    const show = (cat === 'ALL') || (c === cat);
    col.style.display = show ? '' : 'none';
    if (show) visible++;
  });
  document.getElementById('emptyState')?.classList.toggle('d-none', visible !== 0);
}

// ===== 정렬 =====
function sortNotices(mode) {
  const list = document.getElementById('noticeList');
  const items = Array.from(list.querySelectorAll('.col')).filter(el => el.style.display !== 'none');
  items.sort((a,b) => {
    if (mode === 'latest') {
      const da = new Date(a.querySelector('.card-text span')?.textContent || 0);
      const db = new Date(b.querySelector('.card-text span')?.textContent || 0);
      return db - da;
    } else {
      // deadline 가까운 순
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

// ===== 즐겨찾기(LocalStorage) =====
const STAR_KEY = 'hlink_favorites';
function getStars() {
  try { return JSON.parse(localStorage.getItem(STAR_KEY)) || []; } catch { return []; }
}
function saveStars(arr) { localStorage.setItem(STAR_KEY, JSON.stringify(arr)); }

function toggleFavorite(btn) {
  const id = btn.getAttribute('data-id');
  const icon = btn.querySelector('i');
  const stars = getStars();
  const idx = stars.indexOf(id);
  if (idx >= 0) { stars.splice(idx,1); icon.classList.remove('bi-star-fill'); icon.classList.add('bi-star'); }
  else { stars.push(id); icon.classList.remove('bi-star'); icon.classList.add('bi-star-fill'); }
  saveStars(stars);
}
function hydrateStars() {
  const stars = getStars();
  document.querySelectorAll('.star-btn').forEach(btn => {
    const id = btn.getAttribute('data-id');
    const icon = btn.querySelector('i');
    if (stars.includes(id)) { icon.classList.remove('bi-star'); icon.classList.add('bi-star-fill'); }
  });
}

// ===== D-day 표시 보정 =====
function decorateDday() {
  document.querySelectorAll('.col').forEach(col => {
    const deadline = col.getAttribute('data-deadline');
    const pill = col.querySelector('.dday-pill');
    if (!pill || !deadline) return;
    const d = toDdayValue(deadline);
    pill.textContent = (d >= 0) ? `D-${d}` : `D+${Math.abs(d)}`;
    if (d <= 3) pill.classList.add('dday-urgent');
  });
}

// ===== 서버 데이터가 아직 없을 때 위한 데모 렌더 =====
document.addEventListener('DOMContentLoaded', () => {
  const hasServerData = !!document.querySelector('[th\\:each]') === false; // Thymeleaf 렌더 후 th:each 사라짐
  // 서버 렌더가 아니면(=개발 초기) 데모 카드 몇 개 주입
  if (hasServerData) {
    const demo = [
      {id:"1", title:"[학사] 2학기 수강정정 안내", link:"#", date:"2025-10-20", category:"학사", deadline:"2025-10-30", summary:"수강정정 일정 및 유의사항 안내", tags:["수강","정정"]},
      {id:"2", title:"[장학] 외부장학금 신청 공고", link:"#", date:"2025-10-19", category:"장학", deadline:"2025-10-28", summary:"신청 자격 및 제출서류 공지", tags:["장학금","서류"]},
      {id:"3", title:"[SW학부] 캡스톤 공지", link:"#", date:"2025-10-18", category:"SW학부", deadline:"2025-11-05", summary:"팀 구성 및 주제 제출 일정", tags:["캡스톤","팀"]}
    ];
    const list = document.getElementById('noticeList');
    demo.forEach(n => {
      const col = document.createElement('div');
      col.className = 'col';
      col.setAttribute('data-id', n.id);
      col.setAttribute('data-category', n.category);
      col.setAttribute('data-deadline', n.deadline || '');
      col.innerHTML = `
        <div class="card h-100 shadow-sm">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <span class="badge category-badge cat-${n.category}">${n.category}</span>
              <button class="btn btn-sm btn-ghost star-btn" type="button" data-id="${n.id}" onclick="toggleFavorite(this)">
                <i class="bi bi-star"></i>
              </button>
            </div>
            <a href="${n.link}" target="_blank" class="stretched-link text-decoration-none">
              <h6 class="card-title">${n.title}</h6>
            </a>
            <p class="card-text small text-secondary mb-2">
              <i class="bi bi-calendar-event"></i>
              <span>${n.date}</span>
              <span class="ms-2 dday-pill">D-?</span>
            </p>
            <p class="card-text small">${n.summary || '요약 준비 중...'}</p>
            <div class="small text-secondary">
              ${ (n.tags||[]).map(t=>`<span class="tag-badge">#${t}</span>`).join('') }
            </div>
          </div>
        </div>`;
      list.appendChild(col);
    });
  }

  decorateDday();
  hydrateStars();
});

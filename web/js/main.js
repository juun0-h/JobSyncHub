// 메인 JS 파일
const API_BASE_URL = CONFIG.API_BASE_URL;

document.addEventListener('DOMContentLoaded', () => {
  initializeAuthButtons();
  handlePageSpecificFunctions();
});

function initializeAuthButtons() {
  const loginBtn = document.getElementById('login-btn');
  const signupBtn = document.getElementById('signup-btn');
  const userInfo = document.getElementById('user-info');
  const userNameSpan = document.getElementById('user-name');
  const logoutBtn = document.getElementById('logout-btn');

  const accessToken = localStorage.getItem('accessToken');
  const memberInfo = JSON.parse(localStorage.getItem('memberInfo'));

  if (accessToken && memberInfo) {
    // 로그인 상태
    if (loginBtn) loginBtn.style.display = 'none';
    if (signupBtn) signupBtn.style.display = 'none';
    if (userInfo) userInfo.style.display = 'block';
    if (userNameSpan) userNameSpan.textContent = memberInfo.name;
  } else {
    // 비로그인 상태
    if (userInfo) userInfo.style.display = 'none';
    if (loginBtn) loginBtn.style.display = 'inline-block';
    if (signupBtn) signupBtn.style.display = 'inline-block';
  }

  if (loginBtn) loginBtn.addEventListener('click', () => {
    window.location.href = 'login.html';
  });

  if (signupBtn) signupBtn.addEventListener('click', () => {
    window.location.href = 'signup.html';
  });

  if (logoutBtn) logoutBtn.addEventListener('click', () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('memberInfo');
    window.location.href = 'index.html';
  });
}

function handlePageSpecificFunctions() {
  const currentPage = window.location.pathname.split('/').pop();

  if (currentPage === 'login.html') {
    handleLoginPage();
  } else if (currentPage === 'signup.html') {
    handleSignupPage();
  } else if (currentPage === 'search.html') {
    handleSearchPage();
  } else if (currentPage === 'mypage.html') {
    handleMyPage();
  } else if (currentPage === 'subscribe.html') {
    handleSubscribePage();
  }
}

function handleLoginPage() {
  const loginForm = document.getElementById('login-form');
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    const response = await fetch(`${API_BASE_URL}/no_auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });

    const data = await response.json();

    if (data.statusCode === 200) {
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('memberInfo', JSON.stringify(data.memberInfo));
      alert('로그인 성공');
      window.location.href = 'index.html';
    } else {
      alert(`로그인 실패: ${data.message}`);
    }
  });
}

function handleSignupPage() {
  let emailVerified = false;
  let verificationToken = null;

  const requestEmailVerificationBtn = document.getElementById('request-email-verification');
  const verifyEmailButton = document.getElementById('verify-email-button');
  const emailVerificationDiv = document.getElementById('email-verification');
  const signupSubmitBtn = document.getElementById('signup-submit');

  requestEmailVerificationBtn.addEventListener('click', async () => {
    const email = document.getElementById('signup-email').value;

    const response = await fetch(`${API_BASE_URL}/email_auth/auth?email=${encodeURIComponent(email)}`);

    if (response.ok) {
      alert('인증 이메일이 전송되었습니다.');
      emailVerificationDiv.style.display = 'block';
    } else {
      alert('인증 이메일 전송 실패');
    }
  });

  verifyEmailButton.addEventListener('click', async () => {
    const email = document.getElementById('signup-email').value;
    const code = document.getElementById('verification-code').value;

    const response = await fetch(`${API_BASE_URL}/email_auth/verify`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, code })
    });

    const data = await response.json();

    if (data.statusCode === 200) {
      alert('이메일 인증 성공');
      emailVerified = true;
      verificationToken = data.token;
      signupSubmitBtn.disabled = false;
    } else {
      alert(`이메일 인증 실패: ${data.message}`);
    }
  });

  const signupForm = document.getElementById('signup-form');
  signupForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!emailVerified) {
      alert('이메일 인증을 완료해주세요.');
      return;
    }

    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;
    const name = document.getElementById('signup-name').value;

    const response = await fetch(`${API_BASE_URL}/no_auth/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${verificationToken}`
      },
      body: JSON.stringify({
        email,
        password,
        name,
        emailVerified: true
      })
    });

    const data = await response.json();

    if (data.statusCode === 200) {
      alert('회원가입 성공');
      window.location.href = 'login.html';
    } else {
      alert(`회원가입 실패: ${data.message}`);
    }
  });
}

async function fetchWithAuth(url, options = {}) {
  const accessToken = localStorage.getItem('accessToken');

  // /no_auth 또는 /email_auth 경로는 처리하지 않음
  if (url.includes('/no_auth') || url.includes('/email_auth')) {
    return fetch(url, options);
  }

  // 헤더가 없으면 생성
  if (!options.headers) {
    options.headers = {};
  }

  // Authorization 헤더 추가
  options.headers['Authorization'] = `Bearer ${accessToken}`;

  let response = await fetch(url, options);

  // 응답 데이터 파싱
  let data;
  try {
    data = await response.json();
  } catch (e) {
    throw new Error('서버 응답을 파싱하는 중 오류가 발생했습니다.');
  }

  if (response.ok) {
    if (data.statusCode === 200 && data.message === 'success reissued token' && data.accessToken) {
      // 액세스 토큰 재발급
      localStorage.setItem('accessToken', data.accessToken);

      // Authorization 헤더 업데이트
      options.headers['Authorization'] = `Bearer ${data.accessToken}`;

      // 원래의 요청을 다시 실행
      response = await fetch(url, options);

      if (response.ok) {
        data = await response.json();
        return data;
      } else {
        throw new Error(`요청이 실패했습니다: ${response.statusText}`);
      }
    } else {
      return data;
    }
  } else if (response.status === 401) {
    if (data.message === 'expired refresh token' || data.message === 'invalid token') {
      // 로그아웃 처리
      localStorage.removeItem('accessToken');
      localStorage.removeItem('memberInfo');
      alert('로그인이 필요합니다.');
      window.location.href = 'login.html';
    } else {
      throw new Error(`요청이 실패했습니다: ${data.message}`);
    }
  } else {
    throw new Error(`요청이 실패했습니다: ${data.message || response.statusText}`);
  }
}

function handleSearchPage() {
  const jobListingsDiv = document.getElementById('job-listings');
  const searchForm = document.getElementById('search-form');
  const loadMoreBtn = document.getElementById('load-more-btn');
  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    alert('로그인이 필요합니다.');
    window.location.href = 'login.html';
    return;
  }

  let lastSearchAfter = null;
  let currentQueryParams = '';

  // **추가된 코드 시작**

  const techStackButton = document.getElementById('tech-stack-button');
  const techStackMenu = document.getElementById('tech-stack-menu');
  const selectedSkillsDiv = document.getElementById('selected-skills');
  const resetButton = document.getElementById('reset-button');

  // 기술 스택 메뉴 토글
  techStackButton.addEventListener('click', () => {
    techStackMenu.classList.toggle('show');
  });

  // 기술 스택 선택 이벤트 처리
  techStackMenu.addEventListener('change', (e) => {
    if (e.target && e.target.matches('input[type="checkbox"]')) {
      updateSelectedSkills();
    }
  });

  // 메뉴 외부 클릭 시 닫힘 처리
  document.addEventListener('click', (e) => {
    if (!techStackButton.contains(e.target) && !techStackMenu.contains(e.target)) {
      techStackMenu.classList.remove('show');
    }
  });

  // 초기화 버튼 클릭 처리
  resetButton.addEventListener('click', () => {
    searchForm.reset();
    // 기술 스택 체크박스 초기화
    const checkboxes = techStackMenu.querySelectorAll('input[type="checkbox"]');
    checkboxes.forEach(checkbox => {
      checkbox.checked = false;
    });
    // 선택된 기술 스택 태그 초기화
    selectedSkillsDiv.innerHTML = '';
    // lastSearchAfter 초기화
    lastSearchAfter = null;
    // 검색 결과 초기화
    jobListingsDiv.innerHTML = '';
  });

  function updateSelectedSkills() {
    const selectedOptions = Array.from(techStackMenu.querySelectorAll('input[type="checkbox"]:checked'));
    selectedSkillsDiv.innerHTML = '';

    selectedOptions.forEach(option => {
      const skillTag = document.createElement('span');
      skillTag.className = 'badge bg-primary me-1 mb-1';
      skillTag.textContent = option.value;

      // 삭제 버튼 추가
      const removeBtn = document.createElement('button');
      removeBtn.type = 'button';
      removeBtn.className = 'btn-close btn-close-white btn-sm ms-1';
      removeBtn.addEventListener('click', () => {
        option.checked = false;
        updateSelectedSkills();
      });

      skillTag.appendChild(removeBtn);
      selectedSkillsDiv.appendChild(skillTag);
    });
  }

  // **추가된 코드 끝**

  // 초기 로드: 기본 검색 실행
  performSearch();

  // 검색 폼 제출 처리
  searchForm.addEventListener('submit', (e) => {
    e.preventDefault();
    lastSearchAfter = null; // 새로운 검색 시 초기화
    performSearch();
  });

  // 더보기 버튼 클릭 처리
  loadMoreBtn.addEventListener('click', () => {
    performSearch(true);
  });

  function performSearch(isLoadMore = false) {
    // 검색 조건 수집
    const title = document.getElementById('search-title').value.trim();

    // **수정된 부분: 기술 스택 선택 값 가져오기**
    const skills = Array.from(techStackMenu.querySelectorAll('input[type="checkbox"]:checked')).map(input => input.value);

    const experienceSelect = document.getElementById('search-experience');
    const experienceType = experienceSelect.value;

    const sortByDeadlineSelect = document.getElementById('search-sort');
    let sortByDeadline = null;
    if (sortByDeadlineSelect.value !== '') {
      sortByDeadline = parseInt(sortByDeadlineSelect.value);
    }

    // 쿼리 파라미터 생성
    let queryParams = [];
    if (title) {
      queryParams.push(`title=${encodeURIComponent(title)}`);
    }
    if (skills.length > 0) {
      skills.forEach(skill => {
        queryParams.push(`skills=${encodeURIComponent(skill)}`);
      });
    }
    if (experienceType) {
      queryParams.push(`experienceTypes=${encodeURIComponent(experienceType)}`);
    }
    if (sortByDeadline !== null && !isNaN(sortByDeadline)) {
      queryParams.push(`sortByDeadline=${sortByDeadline}`);
    }

    // 기존 검색 후 더보기인 경우 searchAfter 추가
    if (isLoadMore && lastSearchAfter) {
      // searchAfter 값을 쉼표로 구분하여 전달
      const searchAfterParam = lastSearchAfter.map(value => encodeURIComponent(value)).join(',');
      queryParams.push(`searchAfter=${searchAfterParam}`);
    } else {
      // 새로운 검색 시 이전 결과 지우기
      if (!isLoadMore) {
        jobListingsDiv.innerHTML = '';
      }
    }

    currentQueryParams = queryParams.length > 0 ? '?' + queryParams.join('&') : '';

    // 검색 결과 요청
    fetchWithAuth(`${API_BASE_URL}/search/job/search${currentQueryParams}`)
      .then(data => {
        if (data.statusCode === 200) {
          // 검색 결과 표시
          data.jobPostings.forEach(job => {
            const jobCard = document.createElement('div');
            jobCard.classList.add('card', 'mb-3');

            const cardBody = document.createElement('div');
            cardBody.classList.add('card-body');

            cardBody.innerHTML = `
              <h5 class="card-title"><a href="${job.url}" target="_blank">${job.title}</a></h5>
              <h6 class="card-subtitle mb-2 text-muted">${job.company}</h6>
              <p class="card-text">위치: ${job.location}</p>
              <p class="card-text">마감일: ${job.deadline}</p>
              <p class="card-text">경력: ${job.experience}</p>
              <p class="card-text">기술 스택: ${job.skills ? job.skills.join(', ') : ''}</p>
            `;

            jobCard.appendChild(cardBody);
            jobListingsDiv.appendChild(jobCard);
          });

          // searchAfter 값 저장
          if (data.searchAfter && data.jobPostings.length > 0) {
            lastSearchAfter = data.searchAfter;
            loadMoreBtn.style.display = 'block';
          } else {
            lastSearchAfter = null;
            loadMoreBtn.style.display = 'none';
          }
        } else {
          alert(`검색 실패: ${data.message}`);
        }
      })
      .catch(error => {
        console.error('검색 중 오류 발생:', error);
        alert('검색 중 오류가 발생했습니다.');
      });
  }
}

function handleMyPage() {
  const accessToken = localStorage.getItem('accessToken');
  const memberInfo = JSON.parse(localStorage.getItem('memberInfo'));

  if (!accessToken || !memberInfo) {
    alert('로그인이 필요합니다.');
    window.location.href = 'login.html';
    return;
  }

  document.getElementById('mypage-email').value = memberInfo.email;
  document.getElementById('mypage-name').value = memberInfo.name;

  const mypageForm = document.getElementById('mypage-form');
  mypageForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('mypage-name').value;

    try {
      const data = await fetchWithAuth(`${API_BASE_URL}/member/updateMember`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name })
      });

      if (data.statusCode === 200) {
        alert('회원정보가 수정되었습니다.');
        memberInfo.name = name;
        localStorage.setItem('memberInfo', JSON.stringify(memberInfo));
      } else {
        alert(`회원정보 수정 실패: ${data.message}`);
      }
    } catch (error) {
      console.error('회원정보 수정 중 오류 발생:', error);
      alert('회원정보 수정 중 오류가 발생했습니다.');
    }
  });
}

function handleSubscribePage() {
  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    alert('로그인이 필요합니다.');
    window.location.href = 'login.html';
    return;
  }

  const subscribeForm = document.getElementById('subscribe-form');
  const skillsSelect = document.getElementById('subscribe-skills');
  const selectedSkillsDiv = document.getElementById('subscribe-selected-skills');
  const resetButton = document.getElementById('subscribe-reset-button');

  // **추가된 코드 시작**

  // 기술 스택 선택 이벤트 처리
  skillsSelect.addEventListener('change', () => {
    updateSelectedSkills();
  });

  // 초기화 버튼 클릭 처리
  resetButton.addEventListener('click', () => {
    subscribeForm.reset();
    // 선택된 기술 스택 태그 초기화
    selectedSkillsDiv.innerHTML = '';
  });

  function updateSelectedSkills() {
    const selectedOptions = Array.from(skillsSelect.selectedOptions);
    selectedSkillsDiv.innerHTML = '';

    selectedOptions.forEach(option => {
      const skillTag = document.createElement('span');
      skillTag.className = 'badge bg-primary me-1';
      skillTag.textContent = option.value;

      // 삭제 버튼 추가
      const removeBtn = document.createElement('button');
      removeBtn.type = 'button';
      removeBtn.className = 'btn-close btn-close-white btn-sm ms-1';
      removeBtn.addEventListener('click', () => {
        option.selected = false;
        updateSelectedSkills();
      });

      skillTag.appendChild(removeBtn);
      selectedSkillsDiv.appendChild(skillTag);
    });
  }

  // **추가된 코드 끝**

  subscribeForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const techStack = Array.from(skillsSelect.selectedOptions).map(option => option.value);

    try {
      const data = await fetchWithAuth(`${API_BASE_URL}/email/subscribe`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ techStack })
      });

      if (data.statusCode === 200) {
        alert('구독 신청이 완료되었습니다.');
      } else {
        alert(`구독 신청 실패: ${data.message}`);
      }
    } catch (error) {
      console.error('구독 신청 중 오류 발생:', error);
      alert('구독 신청 중 오류가 발생했습니다.');
    }
  });
}

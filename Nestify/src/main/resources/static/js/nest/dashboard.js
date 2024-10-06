let originalCoverImg;

$(document).ready(function() {
	const path = window.location.pathname;
	const segments = path.split('/');
	const collectionId = segments[2] || 0;

	handleDashboardPaths(path, collectionId);
	handleEditPage(path, segments);
	bindModalEvents();
	bindBookmarkEvents();
	bindFilterEvents();
	searchEvents();
});

// 경로에 따른 처리 함수
function handleDashboardPaths(path, collectionId) {
	if (path.includes("/dashboard/0")) {
		getAllBookmarks();
		$("#add-to").text('Add to Unsorted');
		$('#selectedCollectionIcon').html(`<svg class="w-[30px] h-[30px] text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" style="margin-top: -2px;" fill="none" viewBox="0 0 24 24">
			  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m17 21-5-4-5 4V3.889a.92.92 0 0 1 .244-.629.808.808 0 0 1 .59-.26h8.333a.81.81 0 0 1 .589.26.92.92 0 0 1 .244.63V21Z"/>
			</svg>`);
		$('#selectedCollectionName').text("All Bookmarks");
	} else if (path.includes("dashboard/-1")) {
		getUnsortedBookmarks();
		$("#add-to").text('Add to Unsorted');
		$('#selectedCollectionIcon').html(`<svg class="w-[30px] h-[30px] text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" style="margin-top: -2px;" fill="none" viewBox="0 0 24 24">
			  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 13h3.439a.991.991 0 0 1 .908.6 3.978 3.978 0 0 0 7.306 0 .99.99 0 0 1 .908-.6H20M4 13v6a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-6M4 13l2-9h12l2 9"/>
			</svg>`);
		$('#selectedCollectionName').text("Unsorted");
	} else {
		getBookmarksByCollection(collectionId);
		$("#add-to").text(`Add to ${collection.name}`);
		$('#selectedCollectionIcon').css('background-color', collection.colorCode);
		$('#selectedCollectionName').text(collection.name);
	}
}

// 편집 페이지 처리 함수
function handleEditPage(path, segments) {
	if (path.includes('/edit') && path.includes('/bookmark')) {
		const collectionId = segments[2];
		const bookmarkId = segments[4];
		$('.edit-panel').show();
		getBookmarkForEdit(collectionId, bookmarkId);
	}
}

// 모달 이벤트 바인딩
function bindModalEvents() {
	$('#open-bookmark-modal').on('click', openBookmarkModal);
	$(document).on('click', closeBookmarkModalOnClickOutside);
	$('#bookmark-url').keyup(toggleAddButton);
}

// 북마크 관련 이벤트 바인딩
function bindBookmarkEvents() {
	$('#add-to').on('click', addBookmark);
	$(document).on('click', '.delete', deleteBookmark);
	$(document).on('click', '#editImgIconDiv', () => $('#coverImg').trigger('click'));
	$(document).on('change', '#coverImg', previewCoverImage);
	$(document).on('click', '#saveBookmarkBtn', saveEditBookmark);
	$(document).on('click', '#closePanelBtn', closeEditPanel);
}

// 정렬 필터 이벤트 바인딩
function bindFilterEvents() {
	$('.filterbtn').on('click', showFilterContent);
	$(document).on('click', closeFilterOnClickOutside);
	$('input[name="sort"]').on('change', performSearch);
}

function searchEvents() {
    // 검색 버튼 클릭 시 검색 동작
    $('#searchBtn').on('click', performSearch);

    // Enter 키를 눌렀을 때도 검색 동작
    $('input[name="keyword"]').on('keydown', function(event) {
        if (event.key === 'Enter' || event.keyCode === 13) {
            performSearch();
        }
    });
}

// 검색 수행 함수
function performSearch() {
    const path = window.location.pathname;
    const segments = path.split('/');
    const collectionId = segments[2] || 0;

    handleDashboardPaths(path, collectionId);
}

// 모달 열기
function openBookmarkModal() {
	const modal = $('#bookmark-modal');
	const buttonOffset = $(this).offset();
	const buttonHeight = $(this).outerHeight();
	const buttonWidth = $(this).outerWidth();
	const modalWidth = modal.outerWidth();
	let left = buttonOffset.left + buttonWidth - modalWidth;
	let top = buttonOffset.top + buttonHeight;

	// 화면 우측을 넘어갈 경우 위치 조정
	if (left + modalWidth > $(window).width()) {
		left = $(window).width() - modalWidth - 10;
	}

	modal.css({ top: top + 'px', left: left + 'px', display: 'block' });
}

// 모달 닫기 (외부 클릭 시)
function closeBookmarkModalOnClickOutside(e) {
	if (!$(e.target).closest('#bookmark-modal, #open-bookmark-modal').length) {
		closeBookmarkModal();
	}
}

// URL 입력 감지 후 버튼 활성화 토글
function toggleAddButton() {
	$('#add-to').attr('disabled', !$('#bookmark-url').val().length);
}

// 북마크 추가
function addBookmark() {
	const url = $('#bookmark-url').val();
	const path = window.location.pathname;
	const segments = path.split('/');
	const collectionId = segments[2];

	if (!url) {
		alert('Please enter a URL.');
		return;
	}

	const bookmarkEntity = { url };
	const endpoint = collectionId < 1 ? '/api/v1/collection/bookmark' : `/api/v1/collection/${collectionId}/bookmark`;

	$.ajax({
		url: endpoint,
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(bookmarkEntity),
		success: function(bookmark) {
			alert('Bookmark added successfully!');
			closeBookmarkModal();
			location.href = `/dashboard/${collectionId}/bookmark/${bookmark.bookmarkId}/edit`;
		},
		error: function() {
			alert('Failed to add bookmark. Please try again.');
		}
	});
}

// 북마크 삭제
function deleteBookmark() {
	const bookmarkId = $(this).data('id');
	if (confirm('Are you sure you want to delete this bookmark?')) {
		$.ajax({
			url: `/api/v1/bookmark/${bookmarkId}`,
			type: 'DELETE',
			success: function() {
				closeEditPanel();
			},
			error: function(xhr) {
				console.error("Error occurred:", xhr.responseText);
			}
		});
	}
}

// 커버 이미지 미리보기
function previewCoverImage(e) {
	const file = e.target.files[0];
	const reader = new FileReader();

	if (file) {
		reader.onload = function(event) {
			$('.edit-panel #coverImgPreview').attr('src', event.target.result);
		};
		reader.readAsDataURL(file);
	} else {
		$('.edit-panel #coverImgPreview').attr('src', originalCoverImg);
	}
}

// 북마크 수정 저장
function saveEditBookmark() {
	const bookmarkId = $('#saveBookmarkBtn').data('id');
	const formData = new FormData();
	const fileInput = $('.edit-panel #coverImg')[0];

	formData.append('title', $('#title').val());
	formData.append('url', $('#url').val());
	formData.append('collectionId', $('#collection').val());
	formData.append('note', $('#note').val());
	if (fileInput.files.length > 0) {
		formData.append('coverImg', fileInput.files[0]);
	}

	$.ajax({
		url: `/api/v1/bookmark/${bookmarkId}`,
		type: 'PUT',
		data: formData,
		contentType: false,
		processData: false,
		success: function() {
			location.href = `/dashboard/${$('#collection').val()}`;
		},
		error: function(xhr) {
			console.error("Error occurred:", xhr.responseText);
		}
	});
}

// 북마크 수정 패널 닫기
function closeEditPanel() {
	const path = window.location.pathname;
	const collectionId = path.split('/')[2];
	location.href = `/dashboard/${collectionId}`;
}

// 북마크 모달 닫기
function closeBookmarkModal() {
	$('#bookmark-modal').hide();
	$('#bookmark-url').val('');
	$('#add-to').attr('disabled', true);
}

// 북마크 데이터 가져오기 및 처리 함수들
function getAllBookmarks() { fetchBookmarks(`/api/v1/bookmarks/${userId}`, "All Bookmarks"); }
function getUnsortedBookmarks() { fetchBookmarks(`/api/v1/bookmarks/${userId}/collection/-1`, "Unsorted"); }
function getBookmarksByCollection(collectionId) { fetchBookmarks(`/api/v1/bookmarks/${userId}/collection/${collectionId}`, collection.name); }

// 북마크 데이터 요청 및 처리
function fetchBookmarks(endpoint, collectionName) {
	// 라디오 버튼에서 sortBy와 desc 값을 가져옴
    const sortBy = $('input[name="sort"]:checked').val().split('-')[0]; // 정렬 필드 (예: "updated", "created", "title")
    const desc = $('input[name="sort"]:checked').val().split('-')[1] === 'desc'; // true 또는 false 값
    const keyword = encodeURIComponent($('input[name="keyword"]').val().trim()); // 공백과 특수 문자를 인코딩

    // 쿼리스트링을 URL에 추가
    let queryParams = `?sortBy=${sortBy}&desc=${desc}`;
    
    if (keyword != null && keyword != '') {
		queryParams += `&keyword=${keyword}`
	}
    
    let fullUrl = endpoint + queryParams;
	
	$.ajax({
		url: fullUrl,
		type: 'GET',
		success: function(response) {
			const bookmarks = response.content; // Page 객체의 content 배열에 북마크 데이터가 있음
			
			if (bookmarks.length > 0) {
				renderBookmarks(bookmarks);
			} else {
				$('#bookmarksList').html('<div>No bookmarks found in this collection.</div>'); // 북마크가 없을 때 메시지 표시
			}
			$('#selectedCollectionName').text(collectionName); // 컬렉션 이름 표시
		},
		error: function(xhr) {
			console.error("Error occurred:", xhr.responseText);
		}
	});
}

// 북마크 목록 출력
function renderBookmarks(bookmarks) {
	const path = window.location.pathname;
	const segments = path.split('/');
	const collectionId = segments[2];
	
	const selectedLabelText = $('input[name="sort"]:checked').closest('label').text().trim();
	$('.filter-container .filterbtn').text("Sort by " + selectedLabelText)

	$('#bookmarksList').empty();
	let output = '';

	bookmarks.forEach(function(bookmark) {
		output += `<div class="bookmark-item">
			            <div class="bookmark-thumbnail"><img src="${bookmark.coverImgUrl}"></div>
			            <div class="bookmark-details">
			                <div class="bookmark-title"><a href="${bookmark.url}" target="_blank">${bookmark.title}</a></div>
			                <div class="bookmark-info">
			                	<div>${bookmark.collectionName}</div>
			                	<div style="display:inline-block"><span>Updated: </span><span>${formatDateTime(bookmark.updatedAt)}</span></div>
			                	<div style="display:inline-block"> | </div>
			                	<div style="display:inline-block"><span>Created: </span><span>${formatDateTime(bookmark.createdAt)}</span></div>
			                </div>
			            </div>
			            <div class="bookmark-actions">
			                <button class="edit" onclick="location.href='/dashboard/${collectionId}/bookmark/${bookmark.bookmarkId}/edit'">Edit</button>
			                <button class="delete" data-id="${bookmark.bookmarkId}">Delete</button>
			            </div>
			        </div>`;
	});

	$('#bookmarksList').append(output);
	adjustSidebarEditPanelHeight();
}

function getBookmarkForEdit(collectionId, bookmarkId) {
	$.ajax({
		url: `/api/v1/bookmark/${bookmarkId}`,
		type: 'GET',
		dataType: 'JSON',
		success: function(bookmark) {
			$('.edit-panel #coverImgPreview').attr('src', bookmark.coverImgUrl);
			$('.edit-panel #title').val(bookmark.title);
			$('.edit-panel #url').val(bookmark.url);
			$('.edit-panel #note').val(bookmark.note);
			$('#saveBookmarkBtn').data('id', bookmarkId);

			if (bookmark.isSystemCollection == false) {
				$('#collection').val(bookmark.collectionId).change();
			} else {
				$('#collection').val(0).change();
			}

			originalCoverImg = bookmark.coverImgUrl;
		},
		error: function(xhr, status, error) {
			console.error("Error occurred:", error);
			console.error("Response:", xhr.responseText);
		}
	});
}

// 필터 열기
function showFilterContent() {
	$('.filter-content').show();
}

// 필터 닫기 (외부 클릭 시)
function closeFilterOnClickOutside(e) {
    if (!$(e.target).closest('.filter-content, .filterbtn').length) {
        $('.filter-content').hide();
    }
}

// 날짜 포맷팅
function formatDateTime(dateTime) {
	const date = new Date(dateTime);
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	const hours = String(date.getHours()).padStart(2, '0');
	const minutes = String(date.getMinutes()).padStart(2, '0');
	return `${year}/${month}/${day} ${hours}:${minutes}`;
}

function adjustSidebarEditPanelHeight() {
	const mainContentHeight = $('main.main-content').outerHeight();
	$('div.sidebar').css('height', `${mainContentHeight}px`);
	$('div.edit-panel').css('height', `${mainContentHeight}px`);
}

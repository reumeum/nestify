$(document).ready(function () {
	getAllBookmarks();
	
    // 모달 열기
    $('#open-bookmark-modal').on('click', function () {
        const modal = $('#bookmark-modal');
        const buttonOffset = $(this).offset();
        const buttonHeight = $(this).outerHeight();
        const buttonWidth = $(this).outerWidth();
        const modalWidth = modal.outerWidth();

        // 모달의 우측 상단 끝이 버튼의 우측 하단 끝에 위치하도록 계산
        let left = buttonOffset.left + buttonWidth - modalWidth;
        let top = buttonOffset.top + buttonHeight;

        // 화면 우측을 넘어갈 경우 위치 조정
        if (left + modalWidth > $(window).width()) {
            left = $(window).width() - modalWidth - 10; // 화면 끝에서 10px 여유
        }

        modal.css({
            top: top + 'px',
            left: left + 'px',
            display: 'block'
        });
    });

    // 모달 닫기 (외부 클릭 시)
    $(document).on('click', function (e) {
        if (!$(e.target).closest('#bookmark-modal, #open-bookmark-modal').length) {
            closeBookmarkModal();
        }
    });
    
	$('#bookmark-url').keyup(function() {
	    if ($('#bookmark-url').val().length > 0) {
	        $('#add-to-unsorted').removeAttr('disabled'); // 비활성화 해제
	    } else {
	        $('#add-to-unsorted').attr('disabled', true); // 비활성화 설정
	    }
	});

    // Add to Unsorted 버튼 클릭 이벤트
    $('#add-to-unsorted').on('click', function () {
        const url = $('#bookmark-url').val();
        if (url) {
			
			const bookmarkEntity = {
	            url: url
	        };
            
			$.ajax({
				url: '/api/v1/collection/bookmark',
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify(bookmarkEntity),
				success: function(response) {
	                alert('Bookmark added successfully!');
	                getAllBookmarks();
	                closeBookmarkModal();
	            },
	            error: function(xhr, status, error) {
	                alert('Failed to add bookmark. Please try again.');
	            }
			})
            
        } else {
            alert('Please enter a URL.');
        }
    });
});

function getAllBookmarks() {
		$.ajax({
	    url: `/api/v1/bookmarks/${userId}`,
	    type: 'GET',
	    success: function(bookmarks) {
			$('#bookmarksList').empty();
	        let output = '';
	        bookmarks.forEach(function(bookmark) {
	            output += `<div class="bookmark-item">
					            <div class="bookmark-thumbnail"><img src="${bookmark.coverImgUrl}"></div>
					            <div class="bookmark-details">
					                <div class="bookmark-title">${bookmark.title}</div>
					                <div class="bookmark-info">
					                	<div>Unsorted</div>
					                	<div>${bookmark.url}</div>
					                	<div>${bookmark.updated_at}</div>
					                </div>
					            </div>
					            <div class="bookmark-actions">
					                <button class="edit">Edit</button>
					                <button class="delete">Delete</button>
					            </div>
					        </div>`;
	            console.log(bookmark.title);
	        });
	        $('#bookmarksList').append(output);
	        
	        // 이미지 로딩 후 캐시 무효화
			$('#bookmarksList img').each(function() {
			    var imgSrc = $(this).attr('src');
			    $(this).attr('src', imgSrc + '?t=' + new Date().getTime());
			});
			
	        console.log('bookmarks fetched successfully');
	    },
	    error: function(xhr, status, error) {
	        console.error("Error occurred:", error);
	        console.error("Response:", xhr.responseText);
	    }
	});
}

function closeBookmarkModal() {
	$('#bookmark-modal').hide();
	$('#bookmark-url').val('');
	$('#add-to-unsorted').attr('disabled', true);
}

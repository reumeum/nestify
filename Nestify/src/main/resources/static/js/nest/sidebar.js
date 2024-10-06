$(document).ready(function() {

	getCollectionsList();

	/* 북마크 추가 모달 열기 */
	$('#openAddModalBtn').on('click', function() {
		$('#addCollectionModal').css('display', 'flex');
	});

	/* 모달 닫기 */
	$('.close-btn').on('click', closeModal);

	/* 북마크 추가 */
	$('#addCollectionBtn').on('click', function(event) {
		event.preventDefault(); // 기본 폼 제출 방지

		const collectionEntity = {
			colorCode: $('#colorPicker').val(),
			name: $('#collectionName').val(),
			description: $('#collectionDescription').val()
		};

		$.ajax({
			url: '/api/v1/collection',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(collectionEntity),
			success: function(response) {
				alert('Collection added successfully!');
				getCollectionsList();
				closeModal();
			},
			error: function(xhr, status, error) {
				alert('Failed to add collection. Please try again.');
			}
		});
	});

	/* 드롭다운 열기 */
	$('.open-dropdown-btn').on('click', function(event) {
		event.stopPropagation();

		// 현재 열려있는 드롭다운이 있으면 닫기
		$('.dropdown-menu-container').remove();

		const $this = $(this);
		const position = $this.position();

		const personalDropdown = `
			<div class="dropdown-menu-container" style="top: ${position.top + 30}px; left: ${position.left - 46}px;">
			    <ul>			    	
                    <li id="settings">Settings</li>
                    <li id="logout">Logout</li>
                </ul>
			<div>
		`;

		// body에 추가
		$('body').append(personalDropdown);

		$('#logout').on('click', function() {
			$.ajax({
				url: '/logout',
				type: 'post',
				success: function() {
					location.reload();
				},
				error: function(xhr, status, error) {
					alert('Failed to logout. Please try again.');
				}
			})
		});
	});


	/* 컬렉션 리스트 불러오기 */
	function getCollectionsList() {
		$.ajax({
			url: `/api/v1/users/${userId}/collections`,
			type: 'GET',
			success: function(collections) {
				$('.menu > li:contains("Collections") .submenu').empty();
				let output = '';
				collections.forEach(function(collection) {
					output += `<li class="flex-between collection-item" onclick="location.href='/dashboard/${collection.collectionId}'" data-id="${collection.collectionId}" data-color="${collection.colorCode}" data-name="${collection.name}" data-description="${collection.description}">
	            				<div class="flex">
	            					<span class="color-icon" style="background-color: ${collection.colorCode};"></span>
	            					<span class="collection-name">${collection.name}</span>
	            				</div>
	            				<svg style="display:none" class="collection-more w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
								  <path stroke="currentColor" stroke-linecap="round" stroke-width="2" d="M6 12h.01m6 0h.01m5.99 0h.01"/>
								</svg>
	            			</li>`;
				});
				$('.menu > li:contains("Collections") .submenu').append(output);

				$('.collection-more').on('click', function(event) {
					event.stopPropagation();

					// 현재 열려있는 오퍼레이션 메뉴가 있으면 닫기
					$('.operation-menu-container').remove();

					// 클릭한 collection-more 아이콘의 위치 가져오기
					const $this = $(this);
					const position = $this.position();

					// 부모 요소에서 컬렉션 데이터 저장
					const parentItem = $this.closest('.collection-item');
					const collectionId = parentItem.data('id');
					const collectionColor = parentItem.data('color');
					const collectionName = parentItem.data('name');
					const collectionDescription = parentItem.data('description');

					// 오퍼레이션 메뉴 HTML 생성
					const operationMenu = `
	                <div class="operation-menu-container" style="top: ${position.top}px; left: ${position.left + 30}px;">
	                    <ul>
	                        <li class="edit-collection">Edit</li>
	                        <li class="delete-collection">Delete</li>
	                    </ul>
	                </div>
	            `;

					// 오퍼레이션 메뉴를 body에 추가
					$('body').append(operationMenu);
					$this.parent().css('background-color', '#34495e');

					// Edit 클릭 이벤트
					$('.edit-collection').on('click', function() {
						$('#editCollectionModal').css('display', 'flex');
						$('#editCollectionModal #colorPickerEdit').val(collectionColor);
						$('#editCollectionModal #collectionNameEdit').val(collectionName);
						$('#editCollectionModal #collectionDescriptionEdit').val(collectionDescription);
					});

					// 컬렉션 수정
					$('#editCollectionBtn').on('click', function(event) {
						event.preventDefault(); // 기본 폼 제출 방지

						const colorCode = $('#colorPickerEdit').val();
						const name = $('#collectionNameEdit').val();
						const description = $('#collectionDescriptionEdit').val();

						const formData = new FormData;

						formData.append('colorCode', colorCode);
						formData.append('name', name);
						formData.append('description', description);

						$.ajax({
							url: `/api/v1/collection/${collectionId}`,
							type: 'PUT',
							data: formData,
							contentType: false,
							processData: false,
							success: function(response) {
								alert('Collection edited successfully!');
								location.reload();
							},
							error: function(xhr, status, error) {
								alert('Failed to edit collection. Please try again.');
							}
						});
					});

					// 컬렉션 삭제
					$('.delete-collection').on('click', function() {
						if (confirm(`Are you sure you want to delete this collection? All bookmarks within this collection will also be deleted.`)) {
							$.ajax({
								url: `/api/v1/collection/${collectionId}`,
								type: 'DELETE',
								success: function() {
									alert(`Collection ${collectionName} deleted successfully.`);
									location.href = '/dashboard/0';
								},
								error: function(xhr, status, error) {
									console.error("Error occurred:", error);
									console.error("Response:", xhr.responseText);
								}
							});
						};
					});

				});

				console.log('collections fetched successfully');
			},
			error: function(xhr, status, error) {
				console.error("Error occurred:", error);
				console.error("Response:", xhr.responseText);
			}
		});

	}

	// 클릭 시 오퍼레이션 메뉴 닫기
	$(document).on('click', function() {
		$('.operation-menu-container').remove(); // 오퍼레이션 메뉴 제거
		$('.collection-item').css('background-color', '#2c3e50');

		$('.dropdown-menu-container').remove(); // 드롭다운 제거
	});

	function closeModal() {
		const $modal = $('.modal');
		$modal.css('display', 'none');
		$('.modal-input-style').val('');
	}
});

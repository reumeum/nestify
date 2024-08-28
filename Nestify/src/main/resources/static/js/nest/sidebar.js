$(document).ready(function() {
    console.log("userId : " + userId);
    getCollectionsList();

    const $modal = $('#modal');
    const $openModalBtn = $('.open-modal-btn');
    const $closeModalBtn = $('.close-btn');

    $openModalBtn.on('click', function() {
        $modal.css('display', 'flex');
    });

    $closeModalBtn.on('click', closeModal);

    $(window).on('click', function(e) {
        if ($(e.target).is($modal)) {
            closeModal();
        }
    });

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
});

function getCollectionsList() {
	$.ajax({
	    url: `/api/v1/users/${userId}/collections`,
	    type: 'GET',
	    success: function(collections) {
			$('.menu > li:contains("Collections") .submenu').empty();
	        let output = '';
	        collections.forEach(function(collection) {
	            output += `<li class="flex-between collection-item">
	            				<div class="flex">
	            					<span class="color-icon" style="background-color: ${collection.colorCode};"></span>
	            					<a href="/dashboard/${collection.collectionId}" class="collection-name">${collection.name}</a>
	            				</div>
	            				<svg style="display:none" class="collection-more w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
								  <path stroke="currentColor" stroke-linecap="round" stroke-width="2" d="M6 12h.01m6 0h.01m5.99 0h.01"/>
								</svg>
	            			</li>`;
	        });
	        $('.menu > li:contains("Collections") .submenu').append(output);
	        console.log('collections fetched successfully');
	    },
	    error: function(xhr, status, error) {
	        console.error("Error occurred:", error);
	        console.error("Response:", xhr.responseText);
	    }
	});

}

function closeModal() {
    const $modal = $('#modal');
    $modal.css('display', 'none');
    $('#collectionName').val('');
    $('#collectionDescription').val('');
}

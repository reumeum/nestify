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
	        // collections는 JSON 배열로서 각 항목이 CollectionEntity 객체에 해당
	        collections.forEach(function(collection) {
	            console.log("Collection Name: " + collection.name);
	            console.log("Collection Description: " + collection.description);
	        });
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

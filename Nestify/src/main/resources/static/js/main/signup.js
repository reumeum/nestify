const signup = (event) => {
    event.preventDefault(); // 기본 폼 제출 방지
    
    $('#passwordAlert').text('');
    $('#emailAlert').text('');
    $('#passwordAlert').parent().removeAttr('style');
    $('#emailAlert').parent().removeAttr('style');
    
    const password = $('#password').val();
    const confirm_password = $('#confirm_password').val();
    
    if (password != confirm_password) {
		$('#passwordAlert').text('Password do not match');
		$('#passwordAlert').parent().css('margin-top', '-12px').css('margin-left', '5px');
	}

    const userData = {
        username: $('#username').val(),
        email: $('#email').val(),
        password: $('#password').val()
    };

    $.ajax({
        url: '/api/v1/user',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(userData),
        success: function(response) {
            alert('User successfully registered!');
            // 로그인 페이지로 리다이렉트
            window.location.href = '/signin';
        },
        error: function(xhr, status, error) {
	        if (xhr.status === 409) {
				$('#emailAlert').text('User with this email already exists');
				$('#emailAlert').parent().css('margin-top', '-12px').css('margin-left', '5px');
	        }
        }
    });
};

const signup = (event) => {
    event.preventDefault(); // 기본 폼 제출 방지

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
            alert('Signup successful!');
            // 로그인 페이지로 리다이렉트
            window.location.href = '/signin';
        },
        error: function(xhr, status, error) {
            alert('Signup failed.');
        }
    });
};

const signup = async (event) => {
	event.preventDefault(); // 기본 폼 제출 방지
	
    const userData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    const response = await fetch('/api/v1/user', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    });

    if (response.ok) {
        alert('Signup successful!');
        // 로그인 페이지로 리다이렉트
        window.location.href = '/signin';
    } else {
        alert('Signup failed.');
    }
};

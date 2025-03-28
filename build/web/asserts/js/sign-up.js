async function signUp() {

    const user_dto = {
        first_name: document.getElementById("first-name").value,
        last_name: document.getElementById("last-name").value,
        password: document.getElementById("password").value,
        email: document.getElementById("email").value
    };

    const response = await fetch("SignUp",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {"Content-Type": "application/json"}
            }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            window.location = "verify-account.html";
        } else {
            Swal.fire({
                title: "Error",
                text: json.content,
                icon: "error"
            });
        }
    } else {
        Swal.fire({
            title: "Error",
            html: "<p class=\"fs-3\" >Please try again later!</p>",
            icon: "error"
        });
    }

}
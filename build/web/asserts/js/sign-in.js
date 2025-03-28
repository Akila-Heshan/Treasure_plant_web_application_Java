async function signIn() {

    const user_dto = {

        password: document.getElementById("password").value,
        email: document.getElementById("email").value
    };

    const response = await fetch("SignIn",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {"Content-Type": "application/json"}
            }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {
            window.location = "index.html";
        } else {
            if (json.content == "Unverified") {
                window.location = "verify-account.html";
            } else {
                Swal.fire({
                    title: "Error",
                    text: json.content,
                    icon: "error"
                });
            }
        }
    } else {
        Swal.fire({
            title: "Error",
            text: "Please try again later",
            icon: "error"
        });
    }

}

function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
}


function handleCredentialResponse(response) {
    decodeJwtResponse(response.credential);
}
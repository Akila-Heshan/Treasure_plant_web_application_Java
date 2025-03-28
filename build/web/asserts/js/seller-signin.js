async function sellerSignIn() {

    const user_dto = {

        password: document.getElementById("password").value,
        email: document.getElementById("email").value
    };

    const response = await fetch("SellerSignin",
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
            window.location = "seller-home.html";
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
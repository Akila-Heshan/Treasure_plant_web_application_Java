async function verifiAccount() {

    console.log("vfbgfb");

    const dto = {

        verification: document.getElementById("verificationCode").value,
    };

    const response = await fetch("Verification",
            {
                method: "POST",
                body: JSON.stringify(dto),
                headers: {"Content-Type": "application/json"}
            }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {
            window.location = "index.html";
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
            text: "Please try again later",
            icon: "error"
        });
    }

}
 
async function loadCity() {

    const response = await fetch("LoadCity");

    if (response.ok) {

        const json = await response.json();

        let citySelecter = document.getElementById("city");

        json.cityList.forEach(c => {
            let optionTag = document.createElement("option");
            optionTag.value = c.id;
            optionTag.innerHTML = c.name;
            citySelecter.appendChild(optionTag);
        });

    }

}

async function createSeller() {

    const data = {

        line1: document.getElementById("line1").value,
        line2: document.getElementById("line2").value,
        mobile: document.getElementById("mobile").value,
        city: document.getElementById("city").value
    };

    const response = await fetch("CreateSeller",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {"Content-Type": "application/json"}
            }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {
            Swal.fire({
                title: "Success",
                icon: "success"
            });
            
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
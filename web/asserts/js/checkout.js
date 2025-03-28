payhere.onCompleted = async function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);

    const response = await fetch("PaymentSuccess?id=" + orderId);

    if (response.ok) {

        const json = await response.json();

        if (json.success) {
            Swal.fire({
                title: "Success",
                text: json.content,
                icon: "success"
            });
            window.location = "order-history.html";
        } else {
            Swal.fire({
                title: "Error",
                text: "Please Try Again Later",
                icon: "error"
            });
        }

    } else {
        Swal.fire({
            title: "Error",
            text: "Please Try Again Later",
            icon: "error"
        });
    }

};

payhere.onDismissed = function onDismissed() {
    Swal.fire({
        title: "Error",
        text: "Payment Pending",
        icon: "error"
    });
    
    window.location = "order-history.html";
};

payhere.onError = function onError(error) {
    Swal.fire({
        title: "Error",
        text: "Please Try Again Later",
        icon: "error"
    });
};



loadData();

async function loadData() {

    const response = await fetch("LoadCheckout");

    if (response.ok) {

        const json = await  response.json();
        console.log(json);

        if (json.success) {

            const address = json.address;
            let cityList = json.cityList;
            let cartList = json.cartList;

            let citySelecter = document.getElementById("city");

            cityList.forEach(c => {
                let optionTag = document.createElement("option");
                optionTag.value = c.id;
                optionTag.innerHTML = c.name;
                citySelecter.appendChild(optionTag);
            });

            //load current address
            let checkBox = document.getElementById("checkbox1");
            checkBox.addEventListener("change", e => {

                let first_name = document.getElementById("first-name");
                let last_name = document.getElementById("last-name");
                let cityTag = document.getElementById("city");
                let address1 = document.getElementById("address1");
                let address2 = document.getElementById("address2");
                let postalcode = document.getElementById("postal-code");
                let mobile = document.getElementById("mobile");

                if (checkBox.checked) {

                    if (json.isAddress) {

                        first_name.value = address.first_name;
                        last_name.value = address.last_name;
                        cityTag.value = address.city.id;
                        cityTag.disabled = true;
                        address1.value = address.line1;
                        address2.value = address.line2;
                        postalcode.value = address.postal_code;
                        mobile.value = address.mobile;

                    } else {
                        Swal.fire({
                            title: "Error",
                            text: "Current address not found",
                            icon: "error"
                        });
                    }

                } else {
                    first_name.value = "";
                    last_name.value = "";
                    cityTag.value = 0;
                    cityTag.disabled = false;
                    address1.value = "";
                    address2.value = "";
                    postalcode.value = "";
                    mobile.value = "";

//                    cityTag.dispatchEvent(new Event("change"));

                }

            });

            let sub_total = document.getElementById("sub-total");
            let total = document.getElementById("total");
            let shiping = document.getElementById("shiping");

            let sub_total_value = 0;
            let shipping_value = 0;

            cartList.forEach(item => {

                if (item.product.discount != 0) {
                    sub_total_value += (item.product.price - (item.product.price / 100) * item.product.discount) * item.qty;
                } else {
                    sub_total_value += (item.qty * item.product.price);
                }

                if (item.product.weight <= 2) {
                    shipping_value += 1000;
                } else {
                    shipping_value += 2000;
                }

            });

            console.log(shipping_value);
            sub_total.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(sub_total_value);
            shiping.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(shipping_value);
            total.innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(shipping_value + sub_total_value);

        } else {

            window.location = "signIn.html";

        }

    } else {


    }

}

async function checkout() {

    let isCurrentAddress = document.getElementById("checkbox1").checked;

    // get address
    let first_name = document.getElementById("first-name");
    let last_name = document.getElementById("last-name");
    let cityTag = document.getElementById("city");
    let address1 = document.getElementById("address1");
    let address2 = document.getElementById("address2");
    let postalcode = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");

    const date = {
        isCurrentAddress: isCurrentAddress,
        first_name: first_name.value,
        last_name: last_name.value,
        city_id: cityTag.value,
        address1: address1.value,
        address2: address2.value,
        postal_code: postalcode.value,
        mobile: mobile.value
    };

    const response = await fetch("Checkout", {
        method: "POST",
        body: JSON.stringify(date),
        headers: {"Content-Type": "application/json"}
    });

    if (response.ok) {

        const json = await response.json();
        console.log(json);

        if (json.success) {

            payhere.startPayment(json.payhereJson);

            Swal.fire({
                title: "Success",
                html: "<p class=\"fs-3\" >" + json.message + "</p>",
                icon: "success"
            });
        } else {
            Swal.fire({
                title: "Error",
                html: "<p class=\"fs-3\" >" + json.message + "</p>",
                icon: "error"
            });
        }

    } else {
        console.log("Try Again later!");
    }

}
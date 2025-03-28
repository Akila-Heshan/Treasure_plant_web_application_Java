async function loadNav() {

    const response = await fetch("component/nav.html");

    if (response.ok) {

        document.getElementById("nav-set").innerHTML = await response.text();
    }
    
    UserLoad();
    LoadItem();

}

async function LoadItem() {

    const parameters = new URLSearchParams(window.location.search);
    if (parameters.has("id")) {
        const OrderId = parameters.get("id");

        document.getElementById("id").innerHTML = OrderId;

        const response = await fetch("LoadOrderItemHistory?id="+OrderId);

        let cartItemContainer = document.getElementById("item-container");
        let cartItemRow = document.getElementById("item");
        cartItemContainer.innerHTML = "";

        if (response.ok) {
            const json = await response.json();

            console.log(json);

            if (json.length == 0) {
                Swal.fire({
                    title: "Your Order Item History is Empty",
                    text: json.content,
                    icon: "error"
                });
                //window.location = "index.html";
            } else {


                json.forEach(item => {


                    let CartItemRowClone = cartItemRow.cloneNode(true);

                    CartItemRowClone.querySelector("#item-img").src = "ProductImage/" + item.product.id + "/image1.png";
                    CartItemRowClone.querySelector("#item-title").innerHTML = item.product.title;
                    CartItemRowClone.querySelector("#item-qty").innerHTML = item.qty;
                    CartItemRowClone.querySelector("#item-status").innerHTML = item.order_status.name;

                    cartItemContainer.appendChild(CartItemRowClone);
                    cartItemContainer.appendChild(document.createElement("hr"))

                });

            }
        } else {
            Swal.fire({
                title: "Unable to process your Request",
                text: json.content,
                icon: "error"
            });
        }

    } else {
        Swal.fire({
            title: "Unable to process your Request",
            text: json.content,
            icon: "error"
        });
    }
}

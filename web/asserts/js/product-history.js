async function loaSellerNav() {

    console.log("Akila");

    const response = await fetch("component/nav-seller.html");

    if (response.ok) {

        document.getElementById("nav-set").innerHTML = await response.text();
    }

    LoadProductHistory();

}


async function LoadProductHistory() {
    const response = await fetch("LoadProductHistory");

    let cartItemContainer = document.getElementById("cart-item-container");
    let cartItemRow = document.getElementById("cart-item-row");
    cartItemContainer.innerHTML = "";

    if (response.ok) {
        const json = await response.json();

        console.log(json);

        if (json.length == 0) {
            Swal.fire({
                title: "Your Product History is Empty",
                text: json.content,
                icon: "error"
            });
            //window.location = "index.html";
        } else {


            json.forEach(item => {


                let CartItemRowClone = cartItemRow.cloneNode(true);
//                CartItemRowClone.querySelector("#cart-item-a").href = "single-product.html?id=" + item.product.id;
                CartItemRowClone.querySelector("#cart-item-img").src = "ProductImage/" + item.id + "/image1.png";
                CartItemRowClone.querySelector("#cart-item-title").innerHTML = item.title;
                CartItemRowClone.querySelector("#cart-item-qty").innerHTML = item.qty;

//                CartItemRowClone.querySelector("#cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);

                if (item.discount != 0) {
                    CartItemRowClone.querySelector("#cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                    CartItemRowClone.querySelector("#cart-item-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price - (item.price / 100) * item.discount);

                } else {
                    CartItemRowClone.querySelector("#cart-item-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                    CartItemRowClone.querySelector("#cart-item-price-div").remove();

                }



                cartItemContainer.appendChild(CartItemRowClone);
                cartItemContainer.appendChild(document.createElement("hr"))

            });
            document.getElementById("cart-total-qty").innerHTML = totalQty;
            document.getElementById("cart-total").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format((total));
        }
    } else {
        Swal.fire({
            title: "Unable to process your Request",
            text: json.content,
            icon: "error"
        });
    }
}

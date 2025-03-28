function quantityPlus(e) {

    var quantity = document.getElementById(e);

    if (quantity.innerHTML < 8) {

        quantity.innerHTML++
        quantity.innerHTML = quantity.innerHTML++;

    }

}

function quantityMinus(e) {

    var quantity = document.getElementById(e);

    if (quantity.innerHTML > 1) {

        quantity.innerHTML--;
        quantity.innerHTML = quantity.innerHTML--;

    }

}

async function loadNav() {

    const response = await fetch("component/nav.html");

    if (response.ok) {

        document.getElementById("nav-set").innerHTML = await response.text();
    }

}

loadNav();
UserLoad();
loadCartItems();

async function loadCartItems() {
    const response = await fetch("LoadCartItem");

    let cartItemContainer = document.getElementById("cart-item-container");
    let cartItemRow = document.getElementById("cart-item-row");
    cartItemContainer.innerHTML = "";

    if (response.ok) {
        const json = await response.json();

        console.log(json);

        if (json.length == 0) {
            Swal.fire({
                title: "Your Cart is Empty",
                text: json.content,
                icon: "error"
            });
            //window.location = "index.html";
        } else {

            let totalQty = 0;
            let total = 0;

            json.forEach(item => {



                totalQty += item.qty;

                let CartItemRowClone = cartItemRow.cloneNode(true);
                CartItemRowClone.querySelector("#cart-item-a").href = "product-view.html?id=" + item.product.id;
                CartItemRowClone.querySelector("#cart-item-img").src = "ProductImage/" + item.product.id + "/image1.png";
                CartItemRowClone.querySelector("#cart-item-title").innerHTML = item.product.title;

//                CartItemRowClone.querySelector("#cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);

                if (item.product.discount != 0) {
                    CartItemRowClone.querySelector("#cart-item-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);
                    CartItemRowClone.querySelector("#cart-item-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price - (item.product.price / 100) * item.product.discount);

                    let itemSub_total = (item.product.price - (item.product.price / 100) * item.product.discount) * item.qty;
                    total += itemSub_total;

                } else {
                    CartItemRowClone.querySelector("#cart-item-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.product.price);
                    CartItemRowClone.querySelector("#cart-item-price-div").remove();

                    let itemSub_total = item.product.price * item.qty;
                    total += itemSub_total;
                }

                CartItemRowClone.querySelector("#use-qty").innerHTML = item.qty;



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



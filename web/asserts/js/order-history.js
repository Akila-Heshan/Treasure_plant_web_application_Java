async function loadNav() {

    const response = await fetch("component/nav.html");

    if (response.ok) {

        document.getElementById("nav-set").innerHTML = await response.text();
    }

    UserLoad();
    LoadOrder();
}


async function LoadOrder() {
    const response = await fetch("LoadOrderHistory");

    let cartItemContainer = document.getElementById("order-container");
    let cartItemRow = document.getElementById("order");
    cartItemContainer.innerHTML = "";

    if (response.ok) {
        const json = await response.json();

        console.log(json);

        if (json.orderList.length == 0) {
            Swal.fire({
                title: "Your Order History is Empty",
                text: json.content,
                icon: "error"
            });
            //window.location = "index.html";
        } else {


            json.orderList.forEach(item => {


                let CartItemRowClone = cartItemRow.cloneNode(true);
//                CartItemRowClone.querySelector("#cart-item-a").href = "single-product.html?id=" + item.product.id;
                CartItemRowClone.querySelector("#id").innerHTML = item.id;
                CartItemRowClone.querySelector("#mobile").innerHTML = item.address.mobile;
                CartItemRowClone.querySelector("#date-time").innerHTML = item.date_time;
                
                CartItemRowClone.querySelector("#viewProduct").addEventListener("click" , e =>{
                    window.location = "order-item.html?id="+item.id;
                });
                
                CartItemRowClone.querySelector("#address").innerHTML = item.address.line1+" "+item.address.line2+" "+item.address.city.name;

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
}

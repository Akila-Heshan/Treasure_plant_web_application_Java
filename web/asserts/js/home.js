function changePrice() {

    document.getElementById("priceValue").innerHTML = document.getElementById("price").value;

}

async function loadNav() {

    const response = await fetch("component/nav.html");
    if (response.ok) {
        document.getElementById("nav-set").innerHTML = await response.text();
    }
    UserLoad();
    LoadProduct();

}

async function LoadProduct() {

    const response = await fetch("LoadProductIndex");
    if (response.ok) {
        const json = await response.json();
        let ProductHtml = document.getElementById("product");
        document.getElementById("product-container").innerHTML = "";
        
        json.forEach(item => {
            
            let ProductClone = ProductHtml.cloneNode(true);
            ProductClone.querySelector("#product-image").src = "ProductImage/" + item.id + "/image1.png";
            ProductClone.href = "product-view.html?id=" + item.id;
            ProductClone.querySelector("#product-title").innerHTML = item.title;

            if (item.discount != 0) {
                ProductClone.querySelector("#product-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                ProductClone.querySelector("#product-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price - (item.price / 100) * item.discount);
            } else {
                ProductClone.querySelector("#product-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(item.price);
                ProductClone.querySelector("#product-price-div").remove();
            }
            document.getElementById("product-container").appendChild(ProductClone);
        });
    }
}




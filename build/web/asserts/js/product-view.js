
async function loadNav() {

    console.log("Akila");

    const response = await fetch("component/nav.html");

    if (response.ok) {

        document.getElementById("nav-set").innerHTML = await response.text();
    }

}

loadNav();
UserLoad();

function changeImage(e) {
    console.log(e);
    let image_sub = document.getElementById("img-sub-" + e).src;
    document.getElementById("img-main").src = image_sub;
}

async function loadProduct() {

    console.log("dljkf");

    const parameters = new URLSearchParams(window.location.search);
    if (parameters.has("id")) {
        const productId = parameters.get("id");
        const response = await fetch("LoadSingelProduct?id=" + productId + "");
        if (response.ok) {
//            console.log(response);
            const json = await response.json();
            console.log(json);
            const id = json.Product.id;
            document.getElementById("img-main").src = "ProductImage/" + id + "/image1.png";
            document.getElementById("img-sub-1").src = "ProductImage/" + id + "/image1.png";
            document.getElementById("img-sub-2").src = "ProductImage/" + id + "/image2.png";
            document.getElementById("img-sub-3").src = "ProductImage/" + id + "/image3.png";
            document.getElementById("img-sub-4").src = "ProductImage/" + id + "/image4.png";
            document.getElementById("img-sub-5").src = "ProductImage/" + id + "/image5.png";

            document.getElementById("title").innerHTML = json.Product.title;
            document.getElementById("date-time").innerHTML = json.Product.date_time;

            if (json.Product.discount != 0) {
                document.getElementById("discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(json.Product.price);
                document.getElementById("price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(json.Product.price - (json.Product.price / 100) * json.Product.discount);
            } else {
                document.getElementById("price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(json.Product.price);
                document.getElementById("discount").remove();
            }

            document.getElementById("brand").innerHTML = json.Product.brand.name;
            document.getElementById("qty").innerHTML = json.Product.qty;
            document.getElementById("description").innerHTML = json.Product.description;

            document.getElementById("add-to-cart").addEventListener("click",
                    (e) => {
                addToCart(json.Product.id, document.getElementById("use-qty").innerHTML);
                e.preventDefault();
            });

            if (json.review != "not") {
                document.getElementById("review").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 1}).format(json.review);
                document.getElementById("review-count").innerHTML = json.review_count;
            } else {
                document.getElementById("review-container").remove();
            }

            if (json.rating == 1) {
                star1(false);
            } else if (json.rating == 2) {
                star2(false);
            } else if (json.rating == 3) {
                star3(false);
            } else if (json.rating == 4) {
                star4(false);
            } else if (json.rating == 5) {
                star5(false);
            }

            let ProductHtml = document.getElementById("product");
            document.getElementById("product-container").innerHTML = "";

            json.ProductList.forEach(item => {

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

        } else {
//            window.location = "index.html";
        }

    } else {
        window.location = "index.html";
    }
}

function quantityPlus() {

    var quantity = document.getElementById("use-qty");
    quantity.innerHTML++
    quantity.innerHTML = quantity.innerHTML++;
}

function quantityMinus() {

    if (document.getElementById("use-qty").innerHTML != 1){

        var quantity = document.getElementById("use-qty");
        quantity.innerHTML--;
        quantity.innerHTML = quantity.innerHTML--;

    }
}

async function addToCart(id, qty) {

    console.log("add to cart " + id + " Qty " + qty);

    const response = await fetch("AddToCart?id=" + id + "&qty=" + qty);

    if (response.ok) {
        const json = await response.json();
        if (response.ok) {
            Swal.fire({
                position: "center",
                icon: "success",
                title: json.content,
                showConfirmButton: false,
                timer: 1500
            });
        } else {
            Swal.fire({
                title: "Something went Wrong",
                icon: "error",
                text: json.content,
            });
        }

    } else {
        Swal.fire({
            title: "Unable to process your Request",
            icon: "error",
            text: json.content,
        });
    }

}

function star1(e) {

    document.getElementById("s1").style.color = "yellow";
    document.getElementById("s2").style.color = "black";
    document.getElementById("s3").style.color = "black";
    document.getElementById("s4").style.color = "black";
    document.getElementById("s5").style.color = "black";

    if (e) {
        review(1);
    }

}

function star2(e) {

    document.getElementById("s1").style.color = "yellow";
    document.getElementById("s2").style.color = "yellow";
    document.getElementById("s3").style.color = "black";
    document.getElementById("s4").style.color = "black";
    document.getElementById("s5").style.color = "black";
    if (e) {
        review(2);
    }

}
function star3(e) {

    document.getElementById("s1").style.color = "yellow";
    document.getElementById("s2").style.color = "yellow";
    document.getElementById("s3").style.color = "yellow";
    document.getElementById("s4").style.color = "black";
    document.getElementById("s5").style.color = "black";
    if (e) {
        review(3);
    }

}
function star4(e) {

    document.getElementById("s1").style.color = "yellow";
    document.getElementById("s2").style.color = "yellow";
    document.getElementById("s3").style.color = "yellow";
    document.getElementById("s4").style.color = "yellow";
    document.getElementById("s5").style.color = "black";
    if (e) {
        review(4);
    }

}
function star5(e) {

    document.getElementById("s1").style.color = "yellow";
    document.getElementById("s2").style.color = "yellow";
    document.getElementById("s3").style.color = "yellow";
    document.getElementById("s4").style.color = "yellow";
    document.getElementById("s5").style.color = "yellow";
    if (e) {
        review(5);
    }

}


async function review(e) {

    const parameters = new URLSearchParams(window.location.search);
    const productId = parameters.get("id");

    const response = await fetch("ReviewSave?count=" + e + "&productId=" + productId);

    if (response.ok) {

        const json = await response.json();

        if (json.success) {

            Swal.fire({
                position: "center",
                icon: "success",
                title: json.content,
                showConfirmButton: false,
                timer: 1500
            });

        } else {

            Swal.fire({
                position: "center",
                icon: "error",
                title: json.content,
                showConfirmButton: false,
                timer: 1500
            });

        }

    }

}
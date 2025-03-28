async function loadNav() {

    const response = await fetch("component/nav.html");

    if (response.ok) {

        document.getElementById("nav-set").innerHTML = await response.text();
    }
    
    UserLoad();
    loadSearch();

}

function changePrice() {

    document.getElementById("priceValue").innerHTML = document.getElementById("price").value;
    console.log(document.getElementById("brand").value);

}

async function loadSearch() {

    const response = await fetch("LoadSearch");

    if (response.ok) {

        const json = await response.json();
        console.log(json);

        let categorySelecter = document.getElementById("category");
        let brandSelecter = document.getElementById("brand");

        json.categoryList.forEach(c => {
            let optionTag = document.createElement("option");
            optionTag.value = c.id;
            optionTag.innerHTML = c.name;
            categorySelecter.appendChild(optionTag);
        });

        json.brandList.forEach(c => {
            let optionTag = document.createElement("option");
            optionTag.value = c.id;
            optionTag.innerHTML = c.name;
            brandSelecter.appendChild(optionTag);
        });
        
        updateProductView(json)

    }

}


async function searchProduct(firstResult) {

    let category = document.getElementById("category").value;

    let brand = document.getElementById("brand").value;

    let price = document.getElementById("price").value;

    let searchText = document.getElementById("searchText").value;
    let sort = document.getElementById("sort").value;


    console.log(category);
    console.log(brand);
    console.log(price);
    console.log(searchText);
    console.log(sort);

    const data = {

        first_result: firstResult,
        category: category,
        brand: brand,
        price: price,
        searchText: searchText,
        sort: sort

    };

    const response = await fetch("SearchProduct",
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

//            Swal.fire({
//                title: "Search Complete",
//                icon: "success"
//            });

            updateProductView(json);
            //currentPage = 0;

        } else {

            Swal.fire({
                title: "Please Try Again",
                icon: "error"
            });
        }

    } else {
        Swal.fire({
            title: "Please Try Again",
            icon: "error"
        });
    }

}


var item_product = document.getElementById("product");
var pagination_button = document.getElementById("pagination-button");
let currentPage = 0;

function updateProductView(json) {

    //start load product
    let product_container = document.getElementById("product-container");
    product_container.innerHTML = "";

    json.productList.forEach(product => {
        console.log(product.id);
        let product_clone = item_product.cloneNode(true);

        //update details
        product_clone.href = "product-view.html?id=" + product.id;
        product_clone.querySelector("#product-img").src = "ProductImage/" + product.id + "/image1.png";
        product_clone.querySelector("#product-title").innerHTML = product.title;

        if (product.discount != 0) {
            product_clone.querySelector("#product-price").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price);
            product_clone.querySelector("#product-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price - (product.price / 100) * product.discount);
        } else {
            product_clone.querySelector("#product-discount").innerHTML = new Intl.NumberFormat("en-US", {minimumFractionDigits: 2}).format(product.price);
            product_clone.querySelector("#product-price").remove();
        }
        product_container.appendChild(product_clone);
    });

    //start pagination
    let st_pagination_container = document.getElementById("pagination-container");
    st_pagination_container.innerHTML = "";

    let product_count = json.allProductCount;
    let product_per_paeg = 4;

    let pages = Math.ceil(product_count / product_per_paeg);


    //add previous button
    let st_pagination_button_clone_prev = pagination_button.cloneNode(true);
    st_pagination_button_clone_prev.innerHTML = "Prev";

    st_pagination_button_clone_prev.addEventListener("click", e => {
        currentPage--;
        searchProduct(currentPage * 4);

    });

    if (currentPage != 0) {
        st_pagination_container.appendChild(st_pagination_button_clone_prev);
    }

    for (let i = 0; i < pages; i++) {
        let st_pagination_button_clone = pagination_button.cloneNode(true);
        st_pagination_button_clone.innerHTML = i + 1;
        st_pagination_button_clone.addEventListener("click", e => {
            currentPage = i;
            searchProduct(i * 4);
        });

        if (i == currentPage) {
            st_pagination_button_clone.className = "btn btn-success p-2 m-2";
        } else {
            st_pagination_button_clone.className = "btn btn-primary p-2 m-2";
        }

        st_pagination_container.appendChild(st_pagination_button_clone);
    }


    if (currentPage != (pages - 1)) {
        //add Next button
        let st_pagination_button_clone_next = pagination_button.cloneNode(true);
        st_pagination_button_clone_next.innerHTML = "Next";

        st_pagination_button_clone_next.addEventListener("click", e => {
            currentPage++;
            searchProduct(currentPage * 4);
        });

        st_pagination_container.appendChild(st_pagination_button_clone_next);


    }

}
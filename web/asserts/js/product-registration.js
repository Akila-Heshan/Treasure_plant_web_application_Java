async function loaSellerNav(){
    
    
    const response = await fetch("component/nav-seller.html");
    
    if(response.ok){
        
        document.getElementById("nav-set").innerHTML = await response.text();
    }
    
    loadData();
}

async function loadData() {

    const response = await fetch("ProductListingDataLoad");

    if (response.ok) {

        const json = await response.json();
        console.log(json);

        let brandSelecter = document.getElementById("brand");

        json.brandList.forEach(c => {
            let optionTag = document.createElement("option");
            optionTag.value = c.id;
            optionTag.innerHTML = c.name;
            brandSelecter.appendChild(optionTag);
        });

        let categorySelecter = document.getElementById("category");

        json.categoryList.forEach(c => {
            let optionTag = document.createElement("option");
            optionTag.value = c.id;
            optionTag.innerHTML = c.name;
            categorySelecter.appendChild(optionTag);
        });

    }else{
        
        
    }

}

async function productListing() {

    const categorySelectTag = document.getElementById("category");
    const brandSelecterTag = document.getElementById("brand");
    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const qtyTag = document.getElementById("qty");
    const discountTag = document.getElementById("discount");
    const weightTag = document.getElementById("weight");
    const priceTag = document.getElementById("price");
    const image1Tag = document.getElementById("image1");
    const image2Tag = document.getElementById("image2");
    const image3Tag = document.getElementById("image3");
    const image4Tag = document.getElementById("image4");
    const image5Tag = document.getElementById("image5");

    const data = new FormData();
    data.append("categoryId", categorySelectTag.value);
    data.append("BrandId", brandSelecterTag.value);
    data.append("title", titleTag.value);
    data.append("description", descriptionTag.value);
    data.append("qty", qtyTag.value);
    data.append("discount", discountTag.value);
    data.append("weight", weightTag.value);
    data.append("price", priceTag.value);
    data.append("image1", image1Tag.files[0]);
    data.append("image2", image2Tag.files[0]);
    data.append("image3", image3Tag.files[0]);
    data.append("image4", image4Tag.files[0]);
    data.append("image5", image5Tag.files[0]);

    console.log(categorySelectTag.value)
    const response = await fetch("ProductListing",
            {
                method: "POST",
                body: data
            }
    );

    if (response.ok) {
        const json = await response.json();
        console.log(json.content);

        if (json.success) {

            categorySelectTag.value = 0;
            brandSelecterTag.value = 0;
            titleTag.value = "";
            descriptionTag.value = "";
            priceTag.value = "";
            qtyTag.value = 1;
            discountTag.value =  0;
            weightTag.value= "";
            image1Tag.value = null;
            image2Tag.value = null;
            image3Tag.value = null;
            image4Tag.value = null;
            image5Tag.value = null;

            let timerInterval;
            Swal.fire({
                title: "Success",
                icon: "success",
                html: "<p class=\"fs-3\" >" + json.content + "</p>",
                timer: 3500,
                timerProgressBar: true,
                didOpen: () => {
                    Swal.showLoading();
                    const timer = Swal.getPopup().querySelector("b");
                    timerInterval = setInterval(() => {
                        timer.textContent = `${Swal.getTimerLeft()}`;
                    }, 100);
                },
                willClose: () => {
                    clearInterval(timerInterval);
                }
            }).then((result) => {
                /* Read more about handling dismissals below */
                if (result.dismiss === Swal.DismissReason.timer) {
                    console.log("I was closed by the timer");
                }
            });

        } else {

            let timerInterval;
            Swal.fire({
                title: "Error",
                icon: "error",
                html: "<p class=\"fs-3\" >" + json.content + "</p>",
                timer: 3500,
                timerProgressBar: true,
                didOpen: () => {
                    Swal.showLoading();
                    const timer = Swal.getPopup().querySelector("b");
                    timerInterval = setInterval(() => {
                        timer.textContent = `${Swal.getTimerLeft()}`;
                    }, 100);
                },
                willClose: () => {
                    clearInterval(timerInterval);
                }
            }).then((result) => {
                /* Read more about handling dismissals below */
                if (result.dismiss === Swal.DismissReason.timer) {
                    console.log("I was closed by the timer");
                }
            });

        }
    } else {

        let timerInterval;
        Swal.fire({
            title: "Error",
            icon: "error",
            html: "<p class=\"fs-3\" >Please try again later!</p>",
            timer: 3500,
            timerProgressBar: true,
            didOpen: () => {
                Swal.showLoading();
                const timer = Swal.getPopup().querySelector("b");
                timerInterval = setInterval(() => {
                    timer.textContent = `${Swal.getTimerLeft()}`;
                }, 100);
            },
            willClose: () => {
                clearInterval(timerInterval);
            }
        }).then((result) => {
            /* Read more about handling dismissals below */
            if (result.dismiss === Swal.DismissReason.timer) {
                console.log("I was closed by the timer");
            }
        });

    }

}

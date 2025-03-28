async function loaSellerNav(){
    
    console.log("Akila");
    
    const response = await fetch("component/nav-seller.html");
    
    if(response.ok){
        
        document.getElementById("nav-set").innerHTML = await response.text();
    }
    
}
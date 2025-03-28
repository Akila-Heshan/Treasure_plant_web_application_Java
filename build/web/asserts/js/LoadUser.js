async function UserLoad(){
        
    const response = await fetch("LoadUser");
    
    if(response.ok){
        
        const json = await response.json();
        if(json.success){
            document.getElementById("user-name").innerHTML = json.content;
        }else{
            document.getElementById("user").remove();
        }
        
    }
    
}

async function logout(){
    console.log("logout");
    const response = await fetch("LogOut");
    if(response.ok){
        window.location = "signIn.html";
    }
    
}
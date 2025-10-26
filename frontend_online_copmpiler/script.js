let jwtToken = null;
let userClasses = {}; // Global variable to store class names and code

window.onload = async () => {
    jwtToken = localStorage.getItem("jwtToken");

    if (jwtToken) {
        try {
            const res = await fetch("http://localhost:8080/validate-token", {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + jwtToken
                }
            });

            if (res.ok) {
                const data = await res.json();
                
                if (data.valid) {
                    hideModals(); // token is good, show main app
                    await loadUserClasses(); // Load user's saved classes
                    return;
                }
            }

            // if not valid, clear storage & force login
            localStorage.removeItem("jwtToken");
            localStorage.removeItem("email");
            showLogin();

        } catch (error) {
            console.error("Validation request failed:", error);
            localStorage.removeItem("jwtToken");
            showLogin();
        }
    } else {
        showSignup(); // no token stored, show signup/login
    }
};

// Function to load user's saved classes
async function loadUserClasses() {
    try {
        const res = await fetch('http://localhost:8080/file/getClasses', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + jwtToken
            }
        });

        if (res.ok) {
            const data = await res.json();
            userClasses = data; // Store in global variable
            displayProjectsList();
        } else {
            console.error('Failed to load user classes');
        }
    } catch (error) {
        console.error('Error loading user classes:', error);
    }
}

// Function to display projects list in the sidebar
function displayProjectsList() {
    const projectList = document.getElementById('projectList');
    projectList.innerHTML = ''; // Clear existing list

    for (const [filename, code] of Object.entries(userClasses)) {
        const listItem = document.createElement('li');
        listItem.textContent = filename;
        listItem.style.cursor = 'pointer';
        listItem.style.padding = '8px 0';
        listItem.style.borderBottom = '1px solid #ddd';
        listItem.style.listStyle = 'none';
        
        // Add hover effect
        listItem.addEventListener('mouseenter', () => {
            listItem.style.backgroundColor = '#e8e8e8';
        });
        listItem.addEventListener('mouseleave', () => {
            listItem.style.backgroundColor = 'transparent';
        });

        // Add click event to load code into editor
        listItem.addEventListener('click', () => {
            loadClassIntoEditor(filename);
        });

        projectList.appendChild(listItem);
    }
}

// Function to load selected class code into editor
function loadClassIntoEditor(filename) {
    const editor = document.getElementById('editor');
    if (userClasses[filename]) {
        editor.value = userClasses[filename];
        document.getElementById('output').innerText = `Loaded: ${filename}`;
        
        // Highlight selected item
        const projectList = document.getElementById('projectList');
        const items = projectList.children;
        for (let item of items) {
            item.style.backgroundColor = 'transparent';
            item.style.fontWeight = 'normal';
        }
        
        // Find and highlight the clicked item
        for (let item of items) {
            if (item.textContent === filename) {
                item.style.backgroundColor = '#4caf50';
                item.style.color = 'white';
                item.style.fontWeight = 'bold';
                break;
            }
        }
    }
}

function showSignup() {
    document.getElementById('signupModal').style.display = 'flex';
    document.getElementById('loginModal').style.display = 'none';
    document.getElementById('main-content').style.filter = 'blur(7px)';
}
function showLogin() {
    document.getElementById('signupModal').style.display = 'none';
    document.getElementById('loginModal').style.display = 'flex';
    document.getElementById('main-content').style.filter = 'blur(7px)';
}
function hideModals() {
    document.getElementById('signupModal').style.display = 'none';
    document.getElementById('loginModal').style.display = 'none';
    document.getElementById('main-content').style.filter = '';
}

document.getElementById('googleLoginBtn').onclick = () => {
    const domain = "ap-south-1gtxgvcl78.auth.ap-south-1.amazoncognito.com";
    const clientId = "sgkthh9n877js29ue7quf9lok";
    const redirectUri = "https://d84l1y8p4kdic.cloudfront.net";

    const url =
        `https://${domain}/login/continue` +
        `?client_id=${clientId}` +
        `&redirect_uri=${encodeURIComponent(redirectUri)}` +
        `&response_type=code&scope=email+openid`;

    window.open(url, "_blank");
};

// ==== CALLBACK HANDLER ====
window.addEventListener("DOMContentLoaded", async () => {
    const currentUrl = window.location.href;

    // Check if we're on CloudFront callback domain
    if (currentUrl.startsWith("https://d84l1y8p4kdic.cloudfront.net")) {
        const params = new URLSearchParams(window.location.search);
        if (params.has("code")) {
            const code = params.get("code");
            console.log("Auth code found:", code);

            try {
                const res = await fetch(`https://ap-south-1gtxgvcl78.auth.ap-south-1.amazoncognito.com/oauth2/token`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams({
                        grant_type: "authorization_code",
                        client_id: "sgkthh9n877js29ue7quf9lok",
                        code: code,
                        redirect_uri: "http://localhost:3000/callback"
                    })
                });
                const tokens = await res.json();
                console.log("Tokens received:", tokens);

                if (tokens.id_token) {
                    jwtToken = tokens.id_token;
                    localStorage.setItem("jwtToken", jwtToken);
                    localStorage.setItem("access_token", tokens.access_token);
                    hideModals();
                    await loadUserClasses(); // Load classes after Google login
                    document.getElementById("output").innerText = "Google Login Success!";
                } else {
                    alert("Failed to exchange code for tokens");
                }
            } catch (err) {
                console.error("Token exchange failed:", err);
            }
        }
    }
});


// Signup handler with API call
document.getElementById('signupBtn').onclick = async function() {
    const email = document.getElementById('signupEmail').value;
    const username = document.getElementById('signupUsername').value;
    const password = document.getElementById('signupPassword').value;
    try {
        const res = await fetch('http://localhost:8080/users/signUp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, username, password })
        });
        const data = await res.json();
        if (res.ok) {
            alert('Signup success! Please login.');
            showLogin();
        } else {
            alert('Signup failed: ' + (data.message || 'Unknown error'));
        }
    } catch (error) {
        alert('Network error during signup.'+error.message);
    }
};

// Login handler with two API calls: login, then auth
document.getElementById('loginBtn').onclick = async function() {
    const email = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    try {
        // 1. Login endpoint
        const resLogin = await fetch('http://localhost:8080/users/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const dataLogin = await resLogin.json();
        if (resLogin.ok) {
            // 2. Auth endpoint (only if login was successful)
            const resAuth = await fetch('http://localhost:8080/authenticate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email,password }),
            });
            const dataAuth = await resAuth.json();
            if (resAuth.ok && dataAuth.token) {
                jwtToken = dataAuth.token;
                localStorage.setItem('jwtToken', jwtToken);
                localStorage.setItem('email', email);
                hideModals();
                await loadUserClasses(); // Load classes after successful login
                document.getElementById('output').innerText = "Logged in!";
            } else {
                alert('Auth failed: ' + (dataAuth.message || 'No token'));
            }
        } else {
            alert('Login failed: ' + (dataLogin.message || 'Wrong credentials'));
        }
    } catch (error) {
        alert('Network error during signup.'+error.message);
    }
};

// Button functionalities
const editor = document.getElementById('editor');
document.getElementById('clearBtn').onclick = () => {
    editor.value = '';
    // Clear any highlighted project items
    const projectList = document.getElementById('projectList');
    const items = projectList.children;
    for (let item of items) {
        item.style.backgroundColor = 'transparent';
        item.style.color = 'black';
        item.style.fontWeight = 'normal';
    }
};

document.getElementById('runBtn').onclick = async () => {
    try {
    const res = await fetch('http://localhost:8080/file/updateJavaFile', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({
            className: getClassName(editor.value),
            code: editor.value
        })
    });

    const data = await res.json();
    if (res.ok) {
        const outputDiv = document.getElementById('output');
        outputDiv.textContent = "File Saved!!";
        // Refresh the projects list to include any new files
        await loadUserClasses();
    } else {
        document.getElementById('output').innerText =
            'Error: ' + (data.message || 'Save failed');
    }
    } catch (error) {
        document.getElementById('output').innerText = "Network error: " + error.message;
    }   

// ---- SECOND REQUEST ----
    try {
    const res2 = await fetch('http://localhost:8080/file/execute', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwtToken')
        },
        body: JSON.stringify({
            javaFileName: getClassName(editor.value),
        })
    });

    const data2 = await res2.json();
    const outputDiv = document.getElementById('output');
    console.log(data2);
    if (res2.ok) {
        if (data2.body.message === "true") {
            outputDiv.textContent = data2.body.output.trim(); // trim to remove newline
        } else {
            outputDiv.textContent = 'Error: ' + (data2.body.output || 'Execution failed');
        }
    } else {
        outputDiv.textContent = 'Error: ' + (data2.body.output || 'Execution failed');
    }

} catch (error) {
    document.getElementById('output').textContent = "Network error: " + error.message;
}
};

document.getElementById('newClassBtn').onclick = () => {
    const className = prompt("Enter class name:");
    if (className) {
        editor.value = `public class ${className} {\n    public static void main(String[] args) {\n        // code here\n    }\n}`;
        document.getElementById('output').innerText = "";
        
        // Clear any highlighted project items
        const projectList = document.getElementById('projectList');
        const items = projectList.children;
        for (let item of items) {
            item.style.backgroundColor = 'transparent';
            item.style.color = 'black';
            item.style.fontWeight = 'normal';
        }   
    }
};

// Helper: get class name from code
function getClassName(code) {
    const match = code.match(/public\s+class\s+(\w+)/);
    return match ? match[1] : null;
}
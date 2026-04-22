const apiBaseUrl = "http://localhost:8080/api/library";

document.addEventListener("DOMContentLoaded", function () {
    const loggedInUser = localStorage.getItem("username");
    const userId = localStorage.getItem("userId");

    if (document.getElementById("loginForm")) {
        document.getElementById("loginForm").addEventListener("submit", loginUser);
    }
    if (document.getElementById("registerForm")) {
        document.getElementById("registerForm").addEventListener("submit", registerUser);
    }
    if (document.getElementById("addBookForm")) {
        document.getElementById("addBookForm").addEventListener("submit", addBook);
    }
    if (document.getElementById("bookList")) {
        loadBooks();
    }
    if (document.getElementById("logoutButton")) {
        document.getElementById("logoutButton").addEventListener("click", logoutUser);
    }
});

function loginUser(event) {
    event.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch(`${apiBaseUrl}/users/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    })
    .then(response => response.json().then(data => ({ status: response.status, body: data }))) 
    .then(({ status, body }) => {
        if (status === 200) {
            console.log("User logged in:", body);
            localStorage.setItem("username", body.username);
            localStorage.setItem("userId", body.id);
            window.location.href = "catalogue.html";
        } else {
            alert(body.message || "Invalid login");
        }
    })
    .catch(error => console.error("Login error", error));
}

function registerUser(event) {
    event.preventDefault();
    const fullName = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch(`${apiBaseUrl}/users/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fullName, email, username, password })
    })
    .then(response => {
        if (response.ok) {
            window.location.href = "index.html";
        } else {
            alert("Registration failed");
        }
    })
    .catch(error => console.error("Registration error", error));
}

function logoutUser() {
    fetch(`${apiBaseUrl}/users/logout`, { method: "POST" })
    .then(() => {
        localStorage.removeItem("username");
        localStorage.removeItem("userId");
        window.location.href = "index.html";
    });
}

function loadBooks() {
    fetch(`${apiBaseUrl}/books`)
    .then(response => response.json())
    .then(books => {
        console.log("Books:", books);
        const bookList = document.getElementById("bookList");
        bookList.innerHTML = "";
        const userId = localStorage.getItem("userId");

        books.forEach(book => {
            const isBorrowed = book.borrower !== null;
            const isOwnedByUser = book.borrower && book.borrower.id == userId;
            
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${isBorrowed ? `<a href="user.html?borrower=${book.borrower.username}">${book.borrower.username}</a>` : "Available"}</td>
                <td>
                    <button onclick="borrowBook(${book.id})" ${isBorrowed ? "disabled" : ""}>Borrow</button>
                    <button onclick="returnBook(${book.id})" ${isOwnedByUser ? "" : "disabled"}>Return</button>
                </td>
            `;
            bookList.appendChild(row);
        });
    });
}

function borrowBook(bookId) {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("You need to log in to borrow a book.");
        return;
    }

    fetch(`${apiBaseUrl}/books/borrow/${bookId}/users/${userId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" }
    })
    .then(response => {
        if (response.ok) {
            loadBooks();
        } else {
            alert("Failed to borrow book.");
        }
    });
}

function returnBook(bookId) {
    fetch(`${apiBaseUrl}/books/return/${bookId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" }
    })
    .then(response => {
        if (response.ok) {
            loadBooks();
        } else {
            alert("Failed to return book.");
        }
    });
}

function addBook(event) {
    event.preventDefault();
    const title = document.getElementById('title').value;
    const author = document.getElementById('author').value;

    fetch(`${apiBaseUrl}/books`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, author })
    })
    .then(response => {
        if (response.ok) {
            loadBooks();
        } else {
            alert("Failed to add book.");
        }
    });

    document.getElementById('title').value = "";
    document.getElementById('author').value = "";
}

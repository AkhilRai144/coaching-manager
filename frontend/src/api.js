const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const AUTH_KEY = "cm_auth";

export function getAuth() {
    try {
        return JSON.parse(localStorage.getItem(AUTH_KEY));
    } catch {
        return null;
    }
}

export function saveAuth(auth) {
    localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

export function clearAuth() {
    localStorage.removeItem(AUTH_KEY);
}

function messageFrom(data, status) {
    if (data && typeof data.message === "string") {
        return data.message;
    }

    if (data && typeof data === "object") {
        const values = Object.values(data).filter(
            (value) => typeof value === "string"
        );

        if (values.length > 0) {
            return values.join(", ");
        }
    }

    return `Request failed (${status})`;
}

async function request(method, path, body) {
    const auth = getAuth();

    const headers = {};

    if (body !== undefined) {
        headers["Content-Type"] = "application/json";
    }

    if (auth?.token) {
        headers["Authorization"] = `Bearer ${auth.token}`;
    }

    let res;

    try {
        res = await fetch(`${API_URL}${path}`, {
            method: method,
            headers: headers,
            body: body !== undefined ? JSON.stringify(body) : undefined,
        });
    } catch {
        throw new Error("Cannot reach the server. Is the backend running?");
    }

    if (res.status === 204) {
        return null;
    }

    let data = null;

    const text = await res.text();

    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = null;
        }
    }

    if (!res.ok) {
        if (res.status === 401 && auth && path !== "/api/auth/login") {
            clearAuth();
            window.location.href = "/login";
        }

        throw new Error(messageFrom(data, res.status));
    }

    return data;
}

export const api = {
    get: (path) => request("GET", path),
    post: (path, body) => request("POST", path, body),
    put: (path, body) => request("PUT", path, body),
    del: (path) => request("DELETE", path),
};
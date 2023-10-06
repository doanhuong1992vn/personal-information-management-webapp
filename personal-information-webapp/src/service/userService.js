export const saveUser = (user) => {
    localStorage.setItem("user", JSON.stringify(user));
}

export const getUser = () => {
    const user = localStorage.getItem('user');
    if (user) {
        return JSON.parse(user);
    }
    return null;
}

export const removeUser = () => {
    localStorage.removeItem("user");
}

export const getUsername = () => {
    const user = getUser();
    if (user) {
        return user.username;
    }
    return null;
}

export const getToken = () => {
    const user = getUser();
    if (user) {
        return user.token;
    }
    return null;
}

export const getLastLogin = () => {
    const user = getUser();
    if (user) {
        return user.lastLogin ? user.lastLogin : "";
    }
    return "";
}




import {httpRequest} from "./apiConfig.js";
import {LOGIN_API, LOGOUT_API, REGISTER_API} from "./api.js";

export const register = (body) => {
    return httpRequest.post(REGISTER_API, body);
}

export const login = (body) => {
    return httpRequest.post(LOGIN_API, body);
}

export const logout = () => {
    httpRequest.post(LOGOUT_API).then();
}
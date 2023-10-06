import {httpRequest} from "./apiConfig.js";
import {CHECK_USERNAME_API, USER_API} from "./api.js";
import {getUsername} from "../service/userService.js";

export const isExistsUsername = async (username) => {
    let result = null;
    try {
        const response = await httpRequest.get(`${CHECK_USERNAME_API}/${username}`);
        result = response.data.data;
    } catch (e) {
        console.log(e)
    }
    return result;
}

export const getUserInformation = () => {
    return httpRequest.get(`${USER_API}/${getUsername()}`);
}

export const updateProfile = (body) => {
    return httpRequest.put(`${USER_API}/${getUsername()}`, body);
}

export const updatePassword = (body) => {
    return httpRequest.patch(`${USER_API}/${getUsername()}`, body);
}



import axios from 'axios';
import {getToken} from "../service/userService.js";
import {HOST} from "./api.js";

export const httpRequest = axios.create({
    baseURL: HOST,
});

httpRequest.interceptors.request.use(
    function (config) {
        const token = getToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        config.headers['Content-Type'] = 'application/json';
        config.headers['Cache-Control'] = 'no-cache';
        return config;
    },
    function (error) {
        console.error("Hey, Request error:");
        console.error(error)
        return Promise.reject(error);
    }
);






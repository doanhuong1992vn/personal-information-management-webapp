import {MAX_LENGTH, MIN_LENGTH} from "../constant/number.js";

const usernameRegex = /^[a-zA-Z0-9_]{8,20}$/;

export const checkValidUsername = (username) => {
    return username ? usernameRegex.test(username) : false;
}

export const checkValidLength = (text) => {
    if (text) {
        let length = text.length;
        return length >= MIN_LENGTH && length <= MAX_LENGTH;
    }
    return false;
}

const capitalRegex = /^(?=.*[A-Z]).{2,}$/;
const lowerLetterRegex = /^(?=.*[a-z]).{2,}$/;
export const checkContainsCapitalAndLowerLetter = (password) => {
    return password ? capitalRegex.test(password) && lowerLetterRegex.test(password) : false;
}

const symbolRegex = /^(?=.*[~!@#$%^&*]).{2,}$/;
const numberRegex = /^(?=.*\d).{2,}$/;
export const checkContainsSymbolAndNumber = (password) => {
    return password ? symbolRegex.test(password) && numberRegex.test(password) : false;
}
import {format, parse} from 'date-fns';
export const getMinBirthday = () => {
    const now = new Date();
    now.setFullYear(now.getFullYear() - 100);
    return now;
}

export const getMaxBirthday = () => {
    return new Date();
}

export const formatBirthday = (birthday) => {
    return format(birthday, "yyyy-MM-dd");
}

export const parseBirthday = (strBirthday) => {
    const parsedDate = parse(strBirthday, "dd/MM/yyyy", new Date());
    if (isNaN(parsedDate)) {
        return null
    }
    return parsedDate;
}
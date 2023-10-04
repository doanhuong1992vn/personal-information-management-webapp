export const getMinBirthday = () => {
    const now = new Date();
    now.setFullYear(now.getFullYear() - 100);
    return now;
}

export const getMaxBirthday = () => {
    return new Date();
}
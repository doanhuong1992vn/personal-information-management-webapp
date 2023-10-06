import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import RegisterForm from "../component/RegisterForm.jsx";
import {getUser} from "../service/userService.js";
import {HOME_PAGE} from "../constant/page.js";

function RegisterPage() {
    const navigate = useNavigate();

    useEffect(() => {
        if (getUser()) {
            navigate(HOME_PAGE);
        }
    }, []);

    return (
        <RegisterForm />
    );
}

export default RegisterPage;
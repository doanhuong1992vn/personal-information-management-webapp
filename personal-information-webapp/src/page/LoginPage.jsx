import LoginForm from "../component/LoginForm.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {getUser} from "../service/userService.js";
import {HOME_PAGE} from "../constant/page.js";

function LoginPage() {
    const navigate = useNavigate();

    useEffect(() => {
        if (getUser()) {
            navigate(HOME_PAGE);
        }
    }, []);

    return (
            <LoginForm />
    );
}

export default LoginPage;
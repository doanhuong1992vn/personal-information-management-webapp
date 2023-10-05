import {AiOutlineCheck} from "react-icons/ai";
import {BsInfoCircle} from "react-icons/bs";
import {
    INVALID_LENGTH_ERROR,
    MISSING_CAPITAL_OR_LOWER_LETTER_ERROR,
    MISSING_NUMBER_OR_SYMBOL_ERROR
} from "../constant/message.js";

function PasswordInstructions({isValidLength, containsSymbolAndNumber, containsCapitalAndLowerLetter}) {
    return (
        <div className={"flex justify-content-center flex-column"}>
            <div
                className={`${isValidLength ? "text-success" : "text-danger"} flex align-items-center`}>
                <div className={"me-1 mb-1"}>
                    {isValidLength ? <AiOutlineCheck/> : <BsInfoCircle/>}
                </div>
                <span>{INVALID_LENGTH_ERROR}</span>
            </div>
            <div
                className={`${containsSymbolAndNumber ? "text-success" : "text-danger"} flex align-items-center`}>
                <div className={"me-1 mb-1"}>
                    {containsSymbolAndNumber ? <AiOutlineCheck/> : <BsInfoCircle/>}
                </div>
                <span>{MISSING_NUMBER_OR_SYMBOL_ERROR}</span>
            </div>
            <div
                className={`${containsCapitalAndLowerLetter ? "text-success" : "text-danger"} flex align-items-center`}>
                <div className={"me-1 mb-1"}>
                    {containsCapitalAndLowerLetter ? <AiOutlineCheck/> : <BsInfoCircle/>}
                </div>
                <span>{MISSING_CAPITAL_OR_LOWER_LETTER_ERROR}</span>
            </div>
        </div>
    );
}

export default PasswordInstructions;
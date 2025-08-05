// src/app/banks/QuestionBankClient.tsx
"use client";

import { useEffect } from "react";
import { message } from "antd";
import QuestionBankList from "@/components/QuestionBankList";

export default function QuestionBankClient({
                                               questionBankList,
                                               errorMsg,
                                           }: {
    questionBankList: API.QuestionBankVO[];
    errorMsg: string;
}) {
    useEffect(() => {
        if (errorMsg) {
            message.error(errorMsg);
        }
    }, [errorMsg]);

    return <QuestionBankList questionBankList={questionBankList} />;
}

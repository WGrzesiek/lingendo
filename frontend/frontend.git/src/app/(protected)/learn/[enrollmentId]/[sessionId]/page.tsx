import LearningSessionClient from "./LearningSessionClient";

export default function Page({
                                 params,
                             }: {
    params: { enrollmentId: string; sessionId: string };
}) {
    return (
        <LearningSessionClient
            enrollmentId={params.enrollmentId}
            sessionId={params.sessionId}
        />
    );
}

import CourseReviewClient from "./CourseReviewClient";

type PageProps = {
    params: { enrollmentId: string };
};

export default function Page({ params }: PageProps) {
    return <CourseReviewClient enrollmentId={params.enrollmentId} />;
}

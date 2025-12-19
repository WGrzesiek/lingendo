import CoursePageClient from "./CoursePageClient";

export default function CoursePage({
                                     params,
                                   }: {
  params: { enrollmentId: string };
}) {
  return <CoursePageClient enrollmentId={params.enrollmentId} />;
}

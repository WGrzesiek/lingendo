import axios from "axios";
import { redirect } from "next/navigation";

const apiClient = axios.create({
  baseURL: "https://api.example.com",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  withCredentials: true,
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      //   redirect("/login");
      redirect("/home");
    }
    return Promise.reject(error);
  }
);

export default apiClient;

import { COOKIES_KEYS } from "@/utils/cookiesKeys";
import axios from "axios";
import { cookies } from "next/headers";

export const baseUrl = process.env.NEXT_PUBLIC_BACKEND_URL;
const serverUrl = process.env.NEXT_PUBLIC_SERVER;

const publicRoutes = ["/auth/login", "/register"];

export const api = axios.create({
  baseURL: serverUrl,
});

api.interceptors.request.use(
  async (config) => {
    const cookieStore = await cookies();
    const token = cookieStore.get(COOKIES_KEYS.token)?.value;

    const isPublicRoute = publicRoutes.some((route) =>
      config.url?.startsWith(route),
    );

    if (isPublicRoute) {
      return config;
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

import axios from "axios";

const baseUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

export const api = axios.create({
  baseURL: baseUrl,
});

api.interceptors.request.use(
  async (config) => {
    config.url = `/api/proxy${config.url}`;
    return config;
  },
  (error) => Promise.reject(error),
);

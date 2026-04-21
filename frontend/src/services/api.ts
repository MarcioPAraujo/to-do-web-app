import axios from "axios";

export const baseUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

export const api = axios.create({
  baseURL: baseUrl,
});

import { IBackendErrorResponse } from "@/interfaces/BackendErrorResponse";
import { api } from "@/services/api";
import { COOKIES_KEYS } from "@/utils/cookiesKeys";
import { AxiosError, isAxiosError } from "axios";
import { cookies } from "next/headers";
import { NextRequest, NextResponse } from "next/server";

interface IProxyRequestParams {
  params: Promise<{ slug: string[] }>;
}

const isBackendError = (
  error: unknown,
): error is AxiosError<IBackendErrorResponse> => {
  if (!error || typeof error !== "object") {
    return false;
  }

  if (!("response" in error)) {
    return false;
  }

  if (typeof error.response !== "object" || error.response === null) {
    return false;
  }

  if (
    !("status" in error.response) ||
    typeof error.response.status !== "number"
  ) {
    return false;
  }

  return (
    error &&
    typeof error === "object" &&
    "response" in error &&
    typeof error.response === "object" &&
    "status" in error.response
  );
};

export async function handleProxyRequest(
  request: NextRequest,
  { params }: IProxyRequestParams,
) {
  const { slug } = await params;
  const cookieStore = await cookies();
  const token = cookieStore.get(COOKIES_KEYS.token)?.value;

  const path = slug.join("/");
  const { search } = new URL(request.url);

  const body = request.method !== "GET" ? await request.text() : undefined;

  try {
    const response = await api.request({
      method: request.method,
      url: `/${path}${search}`,
      data: body,
      headers: {
        Authorization: token ? `Bearer ${token}` : undefined,
        "Content-Type": request.headers.get("Content-Type") || undefined,
      },
    });
    return new NextResponse(response.data, {
      status: response.status,
    });
  } catch (error) {
    if (isAxiosError(error)) {
      return new NextResponse(
        JSON.stringify({
          message: error.response?.data?.message || error.message,
        }),
        {
          status: error.response?.status || 500,
        },
      );
    }
    // backend error

    if (isBackendError(error)) {
      return new NextResponse(
        JSON.stringify({
          message: error.response?.data.message || "An error occurred",
        }),
        {
          status: error.response?.status || 500,
        },
      );
    }

    // unknown error
    return new NextResponse(
      JSON.stringify({ message: "An unknown error occurred" }),
      {
        status: 500,
      },
    );
  }
}

export const GET = handleProxyRequest;
export const POST = handleProxyRequest;
export const PUT = handleProxyRequest;
export const DELETE = handleProxyRequest;

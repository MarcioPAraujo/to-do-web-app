import { NextRequest, NextResponse } from "next/server";

export const config = {
  matcher: ["/tasks/:path*", "/"],
};

export const middleware = async (request: NextRequest) => {
  const token = request.cookies.get("token")?.value;
  const { pathname } = request.nextUrl;

  if (pathname === "/") {
    if (token) {
      return NextResponse.redirect(new URL("/tasks", request.url));
    }

    return NextResponse.next();
  }

  if (pathname.startsWith("/tasks") && !token) {
    return NextResponse.redirect(new URL("/", request.url));
  }

  return NextResponse.next();
};

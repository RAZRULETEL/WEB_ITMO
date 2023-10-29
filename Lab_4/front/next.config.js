/** @type {import('next').NextConfig} */
const nextConfig = {
    output: 'standalone',
    async rewrites() {
        return [
            {
                source: '/proxy/:path*',
                destination: 'http://backend:8080/:path*',
            },
        ]
    },
};

module.exports = nextConfig

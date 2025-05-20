import { promises as fs } from 'fs';
import path from 'path';

export async function GET(req: Request) {
    try {
        console.log('Request received trees');
        const dirPath = path.join(process.cwd(), 'src', 'data', 'trees');
        const files = await fs.readdir(dirPath);

        // Filter only .json files
        const jsonFiles = files.filter(file => file.endsWith('.json'));

        // Read and parse all JSON files (each file contains an array)
        const allArrays = await Promise.all(
            jsonFiles.map(async (file) => {
                const filePath = path.join(dirPath, file);
                const content = await fs.readFile(filePath, 'utf-8');
                return JSON.parse(content);
            })
        );

        // Merge all arrays into a single array
        const mergedArray = allArrays.flat();

        return new Response(JSON.stringify(mergedArray), {
            status: 200,
            headers: { "Content-Type": "application/json" }
        });
    } catch (error) {
        console.error('Error:', error);
        return new Response('Internal Server Error', { status: 500 });
    }
}
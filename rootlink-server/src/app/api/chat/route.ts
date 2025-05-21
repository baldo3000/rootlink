import { google } from '@ai-sdk/google';
import { generateText, streamText, appendResponseMessages, Message, InvalidPromptError } from 'ai';

const model = google('gemini-2.0-flash', {
    useSearchGrounding: true,
});

export async function POST(req: Request) {
    try {
        console.log('Request received');
        const { messages } = (await req.json()) as { messages: Message[] };
        console.log('Messages:', messages);
        const { response, text } = await generateText({
            model: model,
            messages: messages,
        });
        const newMessages = appendResponseMessages({ messages: messages, responseMessages: response.messages });
        const responseMessage = newMessages[newMessages.length - 1];
        console.log('Response message:', responseMessage);
        return Response.json({ responseMessage });

    } catch (error) {
        if (error instanceof InvalidPromptError || error instanceof SyntaxError) {
            console.error('Invalid prompt error:', error);
            return new Response('Invalid prompt provided', { status: 400 });
        }
        console.error('Error:', error);
        return new Response('Internal Server Error', { status: 500 });
    }
}

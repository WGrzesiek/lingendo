// "use client";
//
// import { Card, CardContent } from "@/components/ui/card";
// import { Badge } from "@/components/ui/badge";
// import { Checkbox } from "@/components/ui/checkbox";
// import { Users, CheckCircle2, BookOpen } from "lucide-react";
// import { CommunityWord } from "../../types/community-word.types";
//
// interface CommunityWordCardProps {
//   word: CommunityWord;
//   isSelected: boolean;
//   onToggle: (wordId: string) => void;
// }
//
// /**
//  * Komponent karty słówka ze społeczności
//  * Wyświetla słówko z możliwością zaznaczenia do dodania
//  */
// export const CommunityWordCard = ({
//   word,
//   isSelected,
//   onToggle,
// }: CommunityWordCardProps) => {
//   return (
//     <Card
//       className={`transition-all cursor-pointer hover:shadow-md ${
//         isSelected ? "border-primary bg-primary/5" : ""
//       }`}
//       onClick={() => onToggle(word.id)}
//     >
//       <CardContent className="p-4">
//         <div className="flex items-start gap-3">
//           <div className="pt-1">
//             <Checkbox
//               checked={isSelected}
//               onCheckedChange={() => onToggle(word.id)}
//               onClick={(e) => e.stopPropagation()}
//             />
//           </div>
//
//           <div className="flex-1 space-y-3">
//             {/* Słówko i tłumaczenia */}
//             <div>
//               <h3 className="font-semibold text-lg mb-2">{word.word}</h3>
//               <div className="flex flex-wrap gap-2">
//                 {word.translations.map((translation, index) => (
//                   <Badge key={index} variant="secondary">
//                     {translation}
//                   </Badge>
//                 ))}
//               </div>
//             </div>
//
//             {/* Zdania przykładowe (jeśli są) */}
//             {word.sentences.length > 0 && (
//               <div className="text-sm space-y-2 pt-2 border-t border-border/40">
//                 {word.sentences.slice(0, 2).map((sentence, index) => (
//                   <div key={index} className="space-y-1">
//                     <p className="text-foreground italic">
//                       &quot;{sentence.sentence}&quot;
//                     </p>
//                     <p className="text-muted-foreground pl-4">
//                       → {sentence.translation}
//                     </p>
//                   </div>
//                 ))}
//                 {word.sentences.length > 2 && (
//                   <p className="text-xs text-muted-foreground">
//                     + {word.sentences.length - 2} więcej zdań
//                   </p>
//                 )}
//               </div>
//             )}
//
//             {/* Metadata */}
//             <div className="flex flex-wrap gap-4 text-xs text-muted-foreground pt-2">
//               <span className="flex items-center gap-1.5">
//                 <Users className="w-3.5 h-3.5" />
//                 {word.usageCount} użytkowników
//               </span>
//               {word.sentences.length > 0 && (
//                 <span className="flex items-center gap-1.5">
//                   <BookOpen className="w-3.5 h-3.5" />
//                   {word.sentences.length} zdań
//                 </span>
//               )}
//               <span>Autor: {word.author}</span>
//             </div>
//           </div>
//
//           {isSelected && (
//             <div className="shrink-0">
//               <div className="bg-primary text-primary-foreground rounded-full p-1">
//                 <CheckCircle2 className="w-5 h-5" />
//               </div>
//             </div>
//           )}
//         </div>
//       </CardContent>
//     </Card>
//   );
// };

import {HOW_IT_WORKS_STEPS} from "../../data/howItWorks.ts";
import {Fragment} from "react";
import {ArrowRight} from "lucide-react";
import StepItem from "./StepItem.tsx";

function HowItWorks() {
    return (
        <section aria-labelledby="how-it-works-title" className="pt-4 pb-16">
            <div className="wrapper">
                <h2 id="how-it-works-title" className="sr-only">
                    How it works
                </h2>
                <ol className="mx-auto flex max-w-400 items-center justify-between gap-6">
                    {HOW_IT_WORKS_STEPS.map((step, index) => (
                        <Fragment key={step.id}>
                            {index > 0 && (
                                <li aria-hidden="true">
                                    <ArrowRight className="size-6 text-ink" />
                                </li>
                            )}
                            <StepItem
                                number = {index+1}
                                title = {step.title}
                                description = {step.description}
                                image = {step.image}
                            />
                        </Fragment>
                    ))}
                </ol>
            </div>
        </section>
    )
}

export default HowItWorks

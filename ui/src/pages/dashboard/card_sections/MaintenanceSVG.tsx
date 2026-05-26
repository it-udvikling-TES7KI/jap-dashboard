type MaintenanceSVGProps = {
    className?: string;
};

export default function MaintenanceSVG({ className }: MaintenanceSVGProps) {
    return (
        <svg
            viewBox="0 0 512 512"
            xmlns="http://www.w3.org/2000/svg"
            className={className}
        >
            <g id="SVGRepo_bgCarrier" strokeWidth="0" />
            <g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round" />
            <g id="SVGRepo_iconCarrier">
                <title>maintenance</title>
                <g id="Page-1" stroke="none" strokeWidth="1" fill="none" fillRule="evenodd">
                    <g id="Combined-Shape" fill={"currentColor"} transform="translate(42.666667, 42.666667)">
                        <path d="M128,7.10542736e-15 C198.692448,7.10542736e-15 256,57.307552 256,128 C256,140.931179 254.082471,153.414494 250.516246,165.181113 L384,298.666667 C407.564149,322.230816 407.564149,360.435851 384,384 C360.435851,407.564149 322.230816,407.564149 298.666667,384 L165.181113,250.516246 C153.414494,254.082471 140.931179,256 128,256 C57.307552,256 7.10542736e-15,198.692448 7.10542736e-15,128 C7.10542736e-15,114.357909 2.13416363,101.214278 6.08683609,88.884763 L66.6347809,149.333333 L126.649,129.346 L129.329,126.666 L149.333333,66.7080586 L88.7145729,6.14152881 C101.0933,2.15385405 114.29512,7.10542736e-15 128,7.10542736e-15 Z" />
                    </g>
                </g>
            </g>
        </svg>
    );
}